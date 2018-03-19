package com.example.interpreter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.interpreter.helpers.MyPreference;
import com.example.interpreter.models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener{
	public static final int RESULT_SPEECH = 1;
	public static final int MY_DATA_CHECK_CODE = 2;
	public static final String TYPE_SPEAK = "speak";
	public static final String TYPE_LISTEN = "listen";
	public static TextToSpeech myTTS;
	public static String[] mLanguages, mLocales;
	public static int mIndexInput, mIndexOutput;

	private Button mButtonInput, mButtonOutput;
	private TextView mTextViewInput, mTextViewOutput;
	private DatabaseReference mMessagesOutputRef;
	private ValueEventListener valueEventListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bindWidget();

		mLanguages = getResources().getStringArray(R.array.languages);
		mLocales = getResources().getStringArray(R.array.locales);
		checkTTSCodeData();
	}

	@Override
	protected void onResume() {
		mIndexInput = MyPreference.getInputLanguageIndex(this);
		mIndexOutput = MyPreference.getOutputLanguageIndex(this);
		mButtonInput.setText(getString(R.string.txt_input, mLanguages[mIndexInput]));
		mButtonOutput.setText(getString(R.string.txt_output, mLanguages[mIndexOutput]));
		super.onResume();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_input:
				showLanguage(TYPE_SPEAK, mIndexInput);
				break;
			case R.id.btn_output:
				showLanguage(TYPE_LISTEN, mIndexOutput);
				break;
			case R.id.txt_microphone:
				if (valueEventListener != null) {
					mMessagesOutputRef.removeEventListener(valueEventListener);
				}
				if (!isSpeechRecognitionActivityPresented(this, mLocales[mIndexInput])) {
					mTextViewInput.setText(R.string.error_support_stt);
					mTextViewOutput.setText(R.string.error_support_stt);
				}
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case RESULT_SPEECH:
				if (resultCode == RESULT_OK && null != data) {
					ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					String msg = text.get(0);
					mTextViewInput.setText(getString(R.string.txt_input, msg));

					String nodeInput = mLocales[mIndexInput].split("-")[0];
					String nodeOutput = mLocales[mIndexOutput].split("-")[0];

					DatabaseReference mMessagesRef = FirebaseDatabase.getInstance().getReference().child("messages");
					DatabaseReference mMessagesInputRef = mMessagesRef.child(nodeInput);

					String msgKey = mMessagesInputRef.push().getKey();
					mMessagesInputRef.child(msgKey).setValue(new Message(msg, false));

					mMessagesOutputRef = mMessagesRef.child(nodeOutput).child(msgKey);
					valueEventListener = new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							Message messages = dataSnapshot.getValue(Message.class);
							if (messages != null) {
								mTextViewOutput.setText(getString(R.string.txt_output, messages.message));
								myTTS.speak(messages.message, TextToSpeech.QUEUE_FLUSH, null, "DEFAULT");
							} else {
								mTextViewOutput.setText(getString(R.string.txt_output, "..."));
							}
						}
						@Override
						public void onCancelled(DatabaseError databaseError) {
							mTextViewOutput.setText(getString(R.string.txt_output, databaseError.getMessage()));
						}
					};
					mMessagesOutputRef.addValueEventListener(valueEventListener);
				}
				break;
			case MY_DATA_CHECK_CODE:
				initialTextToSpeech(resultCode);
				break;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (valueEventListener != null) {
			mMessagesOutputRef.removeEventListener(valueEventListener);
		}
	}

	private void bindWidget() {
		mButtonInput = findViewById(R.id.btn_input);
		mButtonOutput = findViewById(R.id.btn_output);

		mTextViewInput = findViewById(R.id.txt_input);
		mTextViewOutput = findViewById(R.id.txt_output);

		mTextViewInput.setText(getString(R.string.txt_input, "..."));
		mTextViewOutput.setText(getString(R.string.txt_output, "..."));

		mTextViewInput.setMovementMethod(new ScrollingMovementMethod());
		mTextViewOutput.setMovementMethod(new ScrollingMovementMethod());

		mButtonInput.setOnClickListener(this);
		mButtonOutput.setOnClickListener(this);
		findViewById(R.id.txt_microphone).setOnClickListener(this);
	}

	private void showLanguage(final String type, final int index) {
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.select_lang, type))
				.setSingleChoiceItems(mLanguages, index, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int position) {
						if (TYPE_SPEAK.equals(type)) {
							mButtonInput.setText(getString(R.string.txt_input, mLanguages[position]));
							MyPreference.setInputLanguageIndex(MainActivity.this, position);
							mIndexInput = position;
						} else {
							mButtonOutput.setText(getString(R.string.txt_output, mLanguages[position]));
							MyPreference.setOutputLanguageIndex(MainActivity.this, position);
							mIndexOutput = position;
							checkTTSCodeData();
						}
						dialogInterface.dismiss();
					}
				})
				.show();
	}
}