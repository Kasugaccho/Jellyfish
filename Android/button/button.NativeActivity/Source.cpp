#include "DxLib.h"
#include <string.h>

// Java �̃N���X�� int�^�̃����o�[�ϐ����擾����֐�
int GetJavaClassIntValue(const ANativeActivity *NativeActivity, JNIEnv *env, jclass Class, const char *Name)
{
	return env->GetIntField(NativeActivity->clazz, env->GetFieldID(Class, Name, "I"));
}

int android_main(void)
{
	JNIEnv *env;
	const ANativeActivity *NativeActivity;

	int TotalState = 0;

	int Counter = 0;
	double Latitude = 0.0;
	double Longitude = 0.0;
	int GPSState = 0;

	int CameraState = 0;
	int CameraImageSizeX = 0;
	int CameraImageSizeY = 0;
	int CameraRotation = -1;
	int CameraGraphHandle = -1;
	int CameraSoftImageHandle = -1;

	// �w�i���D�F�ɂ���
	SetBackgroundColor(128, 128, 128);

	// �c�w���C�u�����̏�����
	if (DxLib_Init() < 0) return -1;

	// �`���𗠉�ʂɕύX
	SetDrawScreen(DX_SCREEN_BACK);

	// �A�v���� NativeActivity ���擾���Ă���
	NativeActivity = GetNativeActivity();

	// ���C�����[�v
	while (ProcessMessage() == 0)
	{
		// ����ʂ̓��e���N���A
		ClearDrawScreen();

		// �S�̂̐i�s�󋵂ɂ���ď����𕪊�
		switch (TotalState)
		{
			// �ŏ��� GPS �̏��������s��
		case 0:
			// Java �̊֐� StartGPS �̌Ăяo��
		{
			// JavaVM �ƃ\�t�g���s�p�X���b�h���֘A�t��( C++ ���� Java �̋@�\���g�p���邽�߂ɕK�v )
			if (NativeActivity->vm->AttachCurrentThreadAsDaemon(&env, NULL) != JNI_OK)
			{
				return -1;
			}

			// Java �̃N���X gaccho1 ���擾
			jclass jclass_gaccho1 = env->GetObjectClass(NativeActivity->clazz);

			// Java �̃N���X gaccho1 �̃����o�[�֐� StartGPS �� ID ���擾
			jmethodID jmethodID_StartGPS = env->GetMethodID(jclass_gaccho1, "StartGPS", "()V");

			// Java �̃N���X gaccho1 �̃����o�[�֐� StartGPS �̌Ăяo��
			env->CallVoidMethod(NativeActivity->clazz, jmethodID_StartGPS);

			// Java �̃N���X gaccho1 �̎Q�Ƃ��폜
			env->DeleteLocalRef(jclass_gaccho1);

			// JavaVM �ƃ\�t�g���s�p�X���b�h�̊֘A�t���I��
			NativeActivity->vm->DetachCurrentThread();
		}

		TotalState = 1;
		break;

		// GPS �̃Z�b�g�A�b�v���I���܂ő҂�
		case 1:
			if (GPSState != 0)
			{
				// GPS �̃Z�b�g�A�b�v���I�������J�����̏��������s��

				// Java �̊֐� StartCamera �̌Ăяo��
				{
					// JavaVM �ƃ\�t�g���s�p�X���b�h���֘A�t��( C++ ���� Java �̋@�\���g�p���邽�߂ɕK�v )
					if (NativeActivity->vm->AttachCurrentThreadAsDaemon(&env, NULL) != JNI_OK)
					{
						return -1;
					}

					// Java �̃N���X gaccho1 ���擾
					jclass jclass_gaccho1 = env->GetObjectClass(NativeActivity->clazz);

					// Java �̃N���X gaccho1 �̃����o�[�֐� StartCamera �� ID ���擾
					jmethodID jmethodID_StartCamera = env->GetMethodID(jclass_gaccho1, "StartCamera", "()V");

					// Java �̃N���X gaccho1 �̃����o�[�֐� StartCamera �̌Ăяo��
					env->CallVoidMethod(NativeActivity->clazz, jmethodID_StartCamera);

					// Java �̃N���X gaccho1 �̎Q�Ƃ��폜
					env->DeleteLocalRef(jclass_gaccho1);

					// JavaVM �ƃ\�t�g���s�p�X���b�h�̊֘A�t���I��
					NativeActivity->vm->DetachCurrentThread();
				}

				TotalState = 2;
			}
			break;

			// GPS �ƃJ�����̏��������I������炱���ł�邱�Ƃ͓��ɖ���
		case 2:
			break;
		}

		// �f�o�r�̈ʒu���͕p�ɂɂ͍X�V����Ȃ��̂ŁA10�t���[���ɂP��̊Ԋu�ŏ����X�V����
		Counter++;
		if (Counter >= 10)
		{
			// �J�E���^�����Z�b�g
			Counter = 0;

			// JavaVM �ƃ\�t�g���s�p�X���b�h���֘A�t��( C++ ���� Java �̋@�\���g�p���邽�߂ɕK�v )
			if (NativeActivity->vm->AttachCurrentThreadAsDaemon(&env, NULL) != JNI_OK)
			{
				return -1;
			}

			// Java �̃N���X gaccho1 ���擾
			jclass jclass_gaccho1 = env->GetObjectClass(NativeActivity->clazz);

			// Java �̃N���X gaccho1 �̃����o�[�ϐ� mLatitude �� ID ���擾
			jfieldID jfieldID_mLatitude = env->GetFieldID(jclass_gaccho1, "mLatitude", "D");

			// Java �̃N���X gaccho1 �̃����o�[�ϐ� mLatitude �̒l�����[�J���ϐ� Latitude �ɑ��
			Latitude = env->GetDoubleField(NativeActivity->clazz, jfieldID_mLatitude);

			// Java �̃N���X gaccho1 �̃����o�[�ϐ� mLongitude �� ID ���擾
			jfieldID jfieldID_mLongitude = env->GetFieldID(jclass_gaccho1, "mLongitude", "D");

			// Java �̃N���X gaccho1 �̃����o�[�ϐ� mLongitude �̒l�����[�J���ϐ� Longitude �ɑ��
			Longitude = env->GetDoubleField(NativeActivity->clazz, jfieldID_mLongitude);

			// Java �̃N���X gaccho1 �̃����o�[�ϐ� mGPSState �� ID ���擾
			jfieldID jfieldID_mGPSState = env->GetFieldID(jclass_gaccho1, "mGPSState", "I");

			// Java �̃N���X gaccho1 �̃����o�[�ϐ� mGPSState �̒l�����[�J���ϐ� GPSState �ɑ��
			GPSState = env->GetIntField(NativeActivity->clazz, jfieldID_mGPSState);

			// Java �̃N���X gaccho1 �̎Q�Ƃ��폜
			env->DeleteLocalRef(jclass_gaccho1);

			// JavaVM �ƃ\�t�g���s�p�X���b�h�̊֘A�t���I��
			NativeActivity->vm->DetachCurrentThread();
		}

		// GPS�̏�Ԃɂ���ĉ�ʂɕ\��������e��ύX
		switch (GPSState)
		{
		case 0:	// �f�o�r���g�p�\���`�F�b�N��
			DrawString(0, 80, "�f�o�r���g�p�\���`�F�b�N��", GetColor(255, 255, 255));
			break;

		case 1:	// �f�o�r���g�p�\
			DrawString(0, 80, "�f�o�r�͎g�p�\", GetColor(255, 255, 255));

			// �o�x�ƈܓx����ʂɕ`��
			DrawFormatString(0, 120, GetColor(255, 255, 255), "�o�x�@%f", Latitude);
			DrawFormatString(0, 140, GetColor(255, 255, 255), "�ܓx�@%f", Longitude);
			break;

		case 2:	// �f�o�r�͎g�p�s�\
			DrawString(0, 80, "�f�o�r�͎g�p�s�\", GetColor(255, 255, 255));
			break;
		}






		// JNI �֘A�̏���
		{
			// JavaVM �ƃ\�t�g���s�p�X���b�h���֘A�t��( C++ ���� Java �̋@�\���g�p���邽�߂ɕK�v )
			if (NativeActivity->vm->AttachCurrentThreadAsDaemon(&env, NULL) != JNI_OK)
			{
				return -1;
			}

			// Java �̃N���X gaccho1 ���擾
			jclass jclass_gaccho1 = env->GetObjectClass(NativeActivity->clazz);

			// Java �̃N���X gaccho1 �̃����o�[�ϐ� CameraState �̒l���擾
			CameraState = GetJavaClassIntValue(NativeActivity, env, jclass_gaccho1, "mCameraState");

			// �J�������g�p�\�ȏ�ԂɂȂ��Ă�����X�ɏ����擾����
			if (CameraState == 1)
			{
				// Java �̃N���X gaccho1 �� int�^�̃����o�[�ϐ��̒l���擾
				CameraImageSizeX = GetJavaClassIntValue(NativeActivity, env, jclass_gaccho1, "mCameraImageSizeX");
				CameraImageSizeY = GetJavaClassIntValue(NativeActivity, env, jclass_gaccho1, "mCameraImageSizeY");
				CameraRotation = GetJavaClassIntValue(NativeActivity, env, jclass_gaccho1, "mCameraRotation");

				// Java �̃N���X gaccho1 �̃����o�[�ϐ� mCameraYImage �� mCameraUVImage �� ID ���擾
				jfieldID jfieldID_mCameraYImage = env->GetFieldID(jclass_gaccho1, "mCameraYImage", "[B");
				jfieldID jfieldID_mCameraUVImage = env->GetFieldID(jclass_gaccho1, "mCameraUVImage", "[B");

				// Java �̃N���X gaccho1 �̃����o�[�ϐ� mCameraYImage �� mCameraUVImage �̒l���擾
				{
					// �\�t�g�C���[�W�n���h�����܂��쐬����Ă��Ȃ������炱���ō쐬����
					if (CameraSoftImageHandle == -1)
					{
						CameraSoftImageHandle = MakeARGB8ColorSoftImage(CameraImageSizeX, CameraImageSizeY);
					}

					// Y�C���[�W��UV�C���[�W���i�[����Ă���z���JNI�I�u�W�F�N�g���擾
					jbyteArray jbyteArray_CameraYImage = (jbyteArray)env->GetObjectField(NativeActivity->clazz, jfieldID_mCameraYImage);
					jbyteArray jbyteArray_CameraUVImage = (jbyteArray)env->GetObjectField(NativeActivity->clazz, jfieldID_mCameraUVImage);

					// JNI�I�u�W�F�N�g����Y�C���[�W��UV�C���[�W���i�[����Ă��郁�����A�h���X���擾
					jbyte *jbyte_CameraYImage = env->GetByteArrayElements(jbyteArray_CameraYImage, NULL);
					jbyte *jbyte_CameraUVImage = env->GetByteArrayElements(jbyteArray_CameraUVImage, NULL);

					// �\�t�g�C���[�W�n���h����YUV�C���[�W��RGB�C���[�W�ɕϊ����Ȃ���]��
					BYTE *yimg = (BYTE *)jbyte_CameraYImage;
					BYTE *dest = (BYTE *)GetImageAddressSoftImage(CameraSoftImageHandle);
					for (int i = 0; i < CameraImageSizeY; i++)
					{
						BYTE *uvimg = (BYTE *)jbyte_CameraUVImage + (i / 2) * (CameraImageSizeX / 2) * 2;
						int k = 0;
						for (int j = 0; j < CameraImageSizeX; j++)
						{
							int b = (int)(*yimg + (1.732446f * ((int)uvimg[0] - 128)));
							int g = (int)(*yimg - (0.698001f * ((int)uvimg[1] - 128)) - (0.337633f * ((int)uvimg[0] - 128)));
							int r = (int)(*yimg + (1.370705f * ((int)uvimg[1] - 128)));

							dest[0] = (BYTE)(b < 0 ? 0 : (b > 255 ? 255 : b));
							dest[1] = (BYTE)(g < 0 ? 0 : (g > 255 ? 255 : g));
							dest[2] = (BYTE)(r < 0 ? 0 : (r > 255 ? 255 : r));
							dest[3] = 0xff;

							dest += 4;

							yimg++;
							k++;
							if (k == 2)
							{
								k = 0;
								uvimg += 2;
							}
						}
					}

					// Y�C���[�W��UV�C���[�W���i�[����Ă���z��ւ̃A�N�Z�X�̏I��
					env->ReleaseByteArrayElements(jbyteArray_CameraYImage, jbyte_CameraYImage, 0);
					env->ReleaseByteArrayElements(jbyteArray_CameraUVImage, jbyte_CameraUVImage, 0);
				}
			}

			// Java �̃N���X gaccho1 �̎Q�Ƃ��폜
			env->DeleteLocalRef(jclass_gaccho1);

			// JavaVM �ƃ\�t�g���s�p�X���b�h�̊֘A�t���I��
			NativeActivity->vm->DetachCurrentThread();
		}

		// �\�t�g�C���[�W�n���h�����쐬����Ă���ꍇ�̂݉摜�̕`�揈�����s��
		if (CameraSoftImageHandle != -1)
		{
			// ���ɃJ��������擾�����摜��`�悷��ׂ̃O���t�B�b�N�n���h�����쐬����Ă��邩�ǂ����ŏ����𕪊�
			if (CameraGraphHandle == -1)
			{
				// �܂��O���t�B�b�N�n���h�����쐬����Ă��Ȃ�������\�t�g�C���[�W�n���h������O���t�B�b�N�n���h�����쐬����
				CameraGraphHandle = CreateGraphFromSoftImage(CameraSoftImageHandle);
			}
			else
			{
				// ���ɃO���t�B�b�N�n���h�����쐬����Ă�����\�t�g�C���[�W�n���h���̃C���[�W���O���t�B�b�N�n���h���ɓ]��
				ReCreateGraphFromSoftImage(CameraSoftImageHandle, CameraGraphHandle);
			}

			// �J�����̌����ɉ����ĉ�]�����`��
			switch (CameraRotation)
			{
			case 0:
				DrawRotaGraph(320, 480, 1.0, DX_PI * 2.0, CameraGraphHandle, FALSE);
				break;

			case 1:
				DrawRotaGraph(320, 480, 1.0, DX_PI * 1.5, CameraGraphHandle, FALSE);
				break;

			case 2:
				DrawRotaGraph(320, 480, 1.0, DX_PI * 1.0, CameraGraphHandle, FALSE);
				break;

			case 3:
				DrawRotaGraph(320, 480, 1.0, DX_PI * 0.5, CameraGraphHandle, FALSE);
				break;
			}
		}

		// �J�����̏�ԂƉ𑜓x��`��
		DrawFormatString(0, 0, GetColor(255, 255, 255), "CameraState : %s   Size : %d x %d",
			CameraState == 0 ? "������" : (CameraState == 1 ? "�g�p�\" : "�g�p�s�\"),
			CameraImageSizeX, CameraImageSizeY
		);




		// ����ʂ̓��e��\��ʂɔ��f
		ScreenFlip();
	}

	// �c�w���C�u�����̌�n��
	DxLib_End();

	// �\�t�g�̏I��
	return 0;
}