Task-1) Input Validation :-
Q1 should accept numbers only and only number keypad should be opened to answer it
Q2 should accept camera capture only (front-cam preferred), should not accept gallery upload
Q2 responded selfie should be geotagged i.e. Geolocation(latitude,longitude) along with it
Task-2) Audio Recording :-
An audio recording should START when answering Q1 and STOP when hitting the submit button of
that contact-form. This Audio file should be generated in .wav format only.
Task-3) Saving answers
save {"Q1":25, "Q2": "xyz.png", "recording": "abc.wav", submit_time:"2023-07-04 18:30:45"} as
json file, captured xyz.png and recorded abc.wav files to local-storage
Task-4) show the answers in another page :-
a table of 4 columns [Q1, Q2, Recording, Submission] having all answers [number, file, file,
timestamp] in rows


Technologies Used : Kotlin, MVVM, Koin for dependency, Media Recorder Api, Room Database

Here is the Apk Link(drive link) : https://drive.google.com/file/d/11KayhDUATVlSgLVecP4Ge62SARW09bUg/view?usp=sharing
