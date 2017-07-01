package com.example.interpreter;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.interpreter.models.Message;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener{
	public static final int RESULT_SPEECH = 1;
	public static final int MY_DATA_CHECK_CODE = 2;
	public static TextToSpeech myTTS;

	private TextView mTextView;
	private DatabaseReference mMessagesThaiRef;
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
				// TODO: 4.Remove latest listenter

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
					ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					mTextView.setText(text.get(0));
					Message message = new Message(text.get(0), false);

					// TODO: 1.Get database reference (messages)

					// TODO: 2.Push new message (en)

					// TODO: 3.Listen Thai message
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
		// TODO: 5.Remove listener
	}
}