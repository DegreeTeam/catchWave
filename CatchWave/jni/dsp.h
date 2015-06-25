#ifndef DSP_H
#define DSP_H

#include <android/log.h>
#define DataLen    		32
#define FilterLen       9
#define DOWN_FACTOR   	2
#define NO_SOUND_DATA   127

extern void javaConvolution(float* input);
extern void javaUpSampling(char* poper, float* result);
extern void javaLinear_interpolation_filter(float* data);
extern void javaByte_Converter(float* data, char* output);
extern void javaPauseSound(char* input);

#endif
