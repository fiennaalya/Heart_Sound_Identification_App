// Advanced Microcontroller-based Audio Workshop
//
// http://www.pjrc.com/store/audio_tutorial_kit.html
// https://hackaday.io/project/8292-microcontroller-audio-workshop-had-supercon-2015
// 
// Part 2-4: Using The Microphone

#define AUDIO_SAMPLE_RATE_EXACT 8000
///////////////////////////////////
#include <Audio.h>
#include <Wire.h>
#include <SPI.h>
#include <SD.h>
#include <SerialFlash.h>


// GUItool: begin automatically generated code
AudioInputI2S            i2s1;           //xy=182.3333282470703,260.3333282470703
AudioOutputI2S           i2s2;           //xy=387.3333282470703,255.3333282470703
AudioOutputUSB           usb1;           //xy=402.3333282470703,297.3333282470703
AudioConnection          patchCord1(i2s1, 0, i2s2, 0);
AudioConnection          patchCord2(i2s1, 0, usb1, 0);
AudioConnection          patchCord3(i2s1, 1, i2s2, 1);
AudioConnection          patchCord4(i2s1, 1, usb1, 1);
AudioControlSGTL5000     sgtl5000_1;     //xy=203.3333282470703,320.3333282470703
// GUItool: end automatically generated code tes

///////////////////////////////////

void setup() {
  Serial.begin(9600);
  AudioMemory(8);
  sgtl5000_1.enable();
  sgtl5000_1.volume(0.5);
  sgtl5000_1.inputSelect(AUDIO_INPUT_MIC);
  sgtl5000_1.micGain(36);
  delay(1000);

}

void loop() {
  // do nothing
}

