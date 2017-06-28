package com.example.interpreter;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
	public static TextToSpeech myTTS;

	private DatabaseReference mMessagesThaiRef;
	private TextView mTextView;
	private ValueEventListener valueEventListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextView = (TextView) findViewById(R.id.text_view);
		findViewById(R.id.image_view).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.image_view:
				// TODO: Remove old listenter
				if (valueEventListener != null) {
					mMessagesThaiRef.removeEventListener(valueEventListener);
				}

				if (!isSpeechRecognitionActivityPresented(this)) {
					Toast.makeText(this, R.string.error_support_stt, Toast.LENGTH_LONG).show();
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
					// TODO: Get text from speech
					ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					mTextView.setText(text.get(0));
					Message message = new Message(text.get(0), false);

					// TODO: Get database reference
					DatabaseReference mMessagesRef = FirebaseDatabase.getInstance().getReference().child("messages");

					// TODO: Push new message
					DatabaseReference mEngMsgRef = mMessagesRef.child("en");
					String topicKey = mEngMsgRef.push().getKey();
					mEngMsgRef.child(topicKey).setValue(message);

					// TODO: Listen Thai message
					mMessagesThaiRef = mMessagesRef.child("th").child(topicKey);
					valueEventListener = new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							Message messages = dataSnapshot.getValue(Message.class);
							if (messages != null) {
								myTTS.speak(messages.message, TextToSpeech.QUEUE_FLUSH, null);
							}
						}
						@Override
						public void onCancelled(DatabaseError databaseError) {
							Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
						}
					};
					mMessagesThaiRef.addValueEventListener(valueEventListener);
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
		// TODO: Remove listener
		if (valueEventListener != null) {
			mMessagesThaiRef.removeEventListener(valueEventListener);
		}
	}
}