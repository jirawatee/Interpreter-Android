const functions = require('firebase-functions');
const request = require('request-promise');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const TRANSLATE_API_KEY = "<YOUR-TRANSLATE-API-KEY>";
const LANGUAGES = ["en", "th", "zh", "ja", "ko"];

exports.translate = functions.runWith(runtimeOpts).database.ref("/messages/{languageID}/{messageID}").onWrite((change, context) => {
  const snapshot = change.after;
  if (snapshot.val().translated) {
    return null;
  }
  const promises = [];
  for (let i = 0; i < LANGUAGES.length; i++) {
    var language = LANGUAGES[i];
    if (language !== context.params.languageID) {
      promises.push(
        createTranslationPromise(
          context.params.languageID,
          language,
          snapshot
        )
      );
    }
  }
  return Promise.all(promises);
});

function createTranslateUrl(source, target, payload) {
  return `https://www.googleapis.com/language/translate/v2?key=${TRANSLATE_API_KEY}&source=${source}&target=${target}&q=${encodeURIComponent(
    payload
  )}`;
}

function createTranslationPromise(source, target, snapshot) {
  const key = snapshot.key;
  const message = snapshot.val().message;
  return request(createTranslateUrl(source, target, message), {
    resolveWithFullResponse: true
  }).then(response => {
    if (response.statusCode === 200) {
      const data = JSON.parse(response.body).data;
      return admin.database().ref(`/messages/${target}/${key}`).set({
        message: data.translations[0].translatedText,
        translated: true
      });
    }
    throw response.body;
  });
}
