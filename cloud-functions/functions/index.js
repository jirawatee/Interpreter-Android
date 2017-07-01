// Import the required modules and initialize
const functions = require('firebase-functions');
const request = require('request-promise');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// Triggered by Firebase RTDB
const LANGUAGES = ['en', 'th'];
exports.translate = functions.database.ref('/messages/{languageID}/{messageID}').onWrite(event => {
  const snapshot = event.data;
  if (snapshot.val().translated) {
    return;
  }
  const promises = [];
  for (let i = 0; i < LANGUAGES.length; i++) {
    var language = LANGUAGES[i];
    if (language !== event.params.languageID) {
      promises.push(createTranslationPromise(event.params.languageID, language, snapshot));
    }
  }
  return Promise.all(promises);
});

function createTranslateUrl(source, target, payload) {
  return `https://www.googleapis.com/language/translate/v2?key=${functions.config().firebase.apiKey}&source=${source}&target=${target}&q=${payload}`;
}

function createTranslationPromise(source, target, snapshot) {
  const key = snapshot.key;
  const message = snapshot.val().message;
  console.log('KEY', key);
  console.log('MESSAGE', message);
  return request(createTranslateUrl(source, target, message), {resolveWithFullResponse: true}).then(response => {
    if (response.statusCode === 200) {
      const data = JSON.parse(response.body).data;
      return admin.database().ref(`/messages/${target}/${key}`).set({message: data.translations[0].translatedText, translated: true});
    }
    throw response.body;
  });
}
