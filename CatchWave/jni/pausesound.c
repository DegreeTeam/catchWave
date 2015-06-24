#include "com_catchwave_jni_DSPforJNI.h"

void javaPauseSound(char* input) {
	int i;
	for (i = 0; i < DataLen * 2; i++) {
		input[i] = NO_SOUND_DATA;
	}
}

