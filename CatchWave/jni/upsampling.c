#include "com_catchwave_jni_DSPforJNI.h"

void javaUpSampling(char* poper, float* result) {
	int i;
	const float upto = 128.0;
	for (i = 0; i < DataLen; i++) {
		result[DOWN_FACTOR * i + 1] = (float) poper[i] / upto;
	}

}

