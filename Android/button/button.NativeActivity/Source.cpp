#include "DxLib.h"
#include <string.h>

// Java のクラスの int型のメンバー変数を取得する関数
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

	// 背景を灰色にする
	SetBackgroundColor(128, 128, 128);

	// ＤＸライブラリの初期化
	if (DxLib_Init() < 0) return -1;

	// 描画先を裏画面に変更
	SetDrawScreen(DX_SCREEN_BACK);

	// アプリの NativeActivity を取得しておく
	NativeActivity = GetNativeActivity();

	// メインループ
	while (ProcessMessage() == 0)
	{
		// 裏画面の内容をクリア
		ClearDrawScreen();

		// 全体の進行状況によって処理を分岐
		switch (TotalState)
		{
			// 最初に GPS の初期化を行う
		case 0:
			// Java の関数 StartGPS の呼び出し
		{
			// JavaVM とソフト実行用スレッドを関連付け( C++ から Java の機能を使用するために必要 )
			if (NativeActivity->vm->AttachCurrentThreadAsDaemon(&env, NULL) != JNI_OK)
			{
				return -1;
			}

			// Java のクラス gaccho1 を取得
			jclass jclass_gaccho1 = env->GetObjectClass(NativeActivity->clazz);

			// Java のクラス gaccho1 のメンバー関数 StartGPS の ID を取得
			jmethodID jmethodID_StartGPS = env->GetMethodID(jclass_gaccho1, "StartGPS", "()V");

			// Java のクラス gaccho1 のメンバー関数 StartGPS の呼び出し
			env->CallVoidMethod(NativeActivity->clazz, jmethodID_StartGPS);

			// Java のクラス gaccho1 の参照を削除
			env->DeleteLocalRef(jclass_gaccho1);

			// JavaVM とソフト実行用スレッドの関連付け終了
			NativeActivity->vm->DetachCurrentThread();
		}

		TotalState = 1;
		break;

		// GPS のセットアップが終わるまで待つ
		case 1:
			if (GPSState != 0)
			{
				// GPS のセットアップが終わったらカメラの初期化を行う

				// Java の関数 StartCamera の呼び出し
				{
					// JavaVM とソフト実行用スレッドを関連付け( C++ から Java の機能を使用するために必要 )
					if (NativeActivity->vm->AttachCurrentThreadAsDaemon(&env, NULL) != JNI_OK)
					{
						return -1;
					}

					// Java のクラス gaccho1 を取得
					jclass jclass_gaccho1 = env->GetObjectClass(NativeActivity->clazz);

					// Java のクラス gaccho1 のメンバー関数 StartCamera の ID を取得
					jmethodID jmethodID_StartCamera = env->GetMethodID(jclass_gaccho1, "StartCamera", "()V");

					// Java のクラス gaccho1 のメンバー関数 StartCamera の呼び出し
					env->CallVoidMethod(NativeActivity->clazz, jmethodID_StartCamera);

					// Java のクラス gaccho1 の参照を削除
					env->DeleteLocalRef(jclass_gaccho1);

					// JavaVM とソフト実行用スレッドの関連付け終了
					NativeActivity->vm->DetachCurrentThread();
				}

				TotalState = 2;
			}
			break;

			// GPS とカメラの初期化が終わったらここでやることは特に無し
		case 2:
			break;
		}

		// ＧＰＳの位置情報は頻繁には更新されないので、10フレームに１回の間隔で情報を更新する
		Counter++;
		if (Counter >= 10)
		{
			// カウンタをリセット
			Counter = 0;

			// JavaVM とソフト実行用スレッドを関連付け( C++ から Java の機能を使用するために必要 )
			if (NativeActivity->vm->AttachCurrentThreadAsDaemon(&env, NULL) != JNI_OK)
			{
				return -1;
			}

			// Java のクラス gaccho1 を取得
			jclass jclass_gaccho1 = env->GetObjectClass(NativeActivity->clazz);

			// Java のクラス gaccho1 のメンバー変数 mLatitude の ID を取得
			jfieldID jfieldID_mLatitude = env->GetFieldID(jclass_gaccho1, "mLatitude", "D");

			// Java のクラス gaccho1 のメンバー変数 mLatitude の値をローカル変数 Latitude に代入
			Latitude = env->GetDoubleField(NativeActivity->clazz, jfieldID_mLatitude);

			// Java のクラス gaccho1 のメンバー変数 mLongitude の ID を取得
			jfieldID jfieldID_mLongitude = env->GetFieldID(jclass_gaccho1, "mLongitude", "D");

			// Java のクラス gaccho1 のメンバー変数 mLongitude の値をローカル変数 Longitude に代入
			Longitude = env->GetDoubleField(NativeActivity->clazz, jfieldID_mLongitude);

			// Java のクラス gaccho1 のメンバー変数 mGPSState の ID を取得
			jfieldID jfieldID_mGPSState = env->GetFieldID(jclass_gaccho1, "mGPSState", "I");

			// Java のクラス gaccho1 のメンバー変数 mGPSState の値をローカル変数 GPSState に代入
			GPSState = env->GetIntField(NativeActivity->clazz, jfieldID_mGPSState);

			// Java のクラス gaccho1 の参照を削除
			env->DeleteLocalRef(jclass_gaccho1);

			// JavaVM とソフト実行用スレッドの関連付け終了
			NativeActivity->vm->DetachCurrentThread();
		}

		// GPSの状態によって画面に表示する内容を変更
		switch (GPSState)
		{
		case 0:	// ＧＰＳが使用可能かチェック中
			DrawString(0, 80, "ＧＰＳが使用可能かチェック中", GetColor(255, 255, 255));
			break;

		case 1:	// ＧＰＳが使用可能
			DrawString(0, 80, "ＧＰＳは使用可能", GetColor(255, 255, 255));

			// 経度と緯度を画面に描画
			DrawFormatString(0, 120, GetColor(255, 255, 255), "経度　%f", Latitude);
			DrawFormatString(0, 140, GetColor(255, 255, 255), "緯度　%f", Longitude);
			break;

		case 2:	// ＧＰＳは使用不可能
			DrawString(0, 80, "ＧＰＳは使用不可能", GetColor(255, 255, 255));
			break;
		}






		// JNI 関連の処理
		{
			// JavaVM とソフト実行用スレッドを関連付け( C++ から Java の機能を使用するために必要 )
			if (NativeActivity->vm->AttachCurrentThreadAsDaemon(&env, NULL) != JNI_OK)
			{
				return -1;
			}

			// Java のクラス gaccho1 を取得
			jclass jclass_gaccho1 = env->GetObjectClass(NativeActivity->clazz);

			// Java のクラス gaccho1 のメンバー変数 CameraState の値を取得
			CameraState = GetJavaClassIntValue(NativeActivity, env, jclass_gaccho1, "mCameraState");

			// カメラが使用可能な状態になっていたら更に情報を取得する
			if (CameraState == 1)
			{
				// Java のクラス gaccho1 の int型のメンバー変数の値を取得
				CameraImageSizeX = GetJavaClassIntValue(NativeActivity, env, jclass_gaccho1, "mCameraImageSizeX");
				CameraImageSizeY = GetJavaClassIntValue(NativeActivity, env, jclass_gaccho1, "mCameraImageSizeY");
				CameraRotation = GetJavaClassIntValue(NativeActivity, env, jclass_gaccho1, "mCameraRotation");

				// Java のクラス gaccho1 のメンバー変数 mCameraYImage と mCameraUVImage の ID を取得
				jfieldID jfieldID_mCameraYImage = env->GetFieldID(jclass_gaccho1, "mCameraYImage", "[B");
				jfieldID jfieldID_mCameraUVImage = env->GetFieldID(jclass_gaccho1, "mCameraUVImage", "[B");

				// Java のクラス gaccho1 のメンバー変数 mCameraYImage と mCameraUVImage の値を取得
				{
					// ソフトイメージハンドルがまだ作成されていなかったらここで作成する
					if (CameraSoftImageHandle == -1)
					{
						CameraSoftImageHandle = MakeARGB8ColorSoftImage(CameraImageSizeX, CameraImageSizeY);
					}

					// YイメージとUVイメージが格納されている配列のJNIオブジェクトを取得
					jbyteArray jbyteArray_CameraYImage = (jbyteArray)env->GetObjectField(NativeActivity->clazz, jfieldID_mCameraYImage);
					jbyteArray jbyteArray_CameraUVImage = (jbyteArray)env->GetObjectField(NativeActivity->clazz, jfieldID_mCameraUVImage);

					// JNIオブジェクトからYイメージとUVイメージが格納されているメモリアドレスを取得
					jbyte *jbyte_CameraYImage = env->GetByteArrayElements(jbyteArray_CameraYImage, NULL);
					jbyte *jbyte_CameraUVImage = env->GetByteArrayElements(jbyteArray_CameraUVImage, NULL);

					// ソフトイメージハンドルにYUVイメージをRGBイメージに変換しながら転送
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

					// YイメージとUVイメージが格納されている配列へのアクセスの終了
					env->ReleaseByteArrayElements(jbyteArray_CameraYImage, jbyte_CameraYImage, 0);
					env->ReleaseByteArrayElements(jbyteArray_CameraUVImage, jbyte_CameraUVImage, 0);
				}
			}

			// Java のクラス gaccho1 の参照を削除
			env->DeleteLocalRef(jclass_gaccho1);

			// JavaVM とソフト実行用スレッドの関連付け終了
			NativeActivity->vm->DetachCurrentThread();
		}

		// ソフトイメージハンドルが作成されている場合のみ画像の描画処理を行う
		if (CameraSoftImageHandle != -1)
		{
			// 既にカメラから取得した画像を描画する為のグラフィックハンドルが作成されているかどうかで処理を分岐
			if (CameraGraphHandle == -1)
			{
				// まだグラフィックハンドルが作成されていなかったらソフトイメージハンドルからグラフィックハンドルを作成する
				CameraGraphHandle = CreateGraphFromSoftImage(CameraSoftImageHandle);
			}
			else
			{
				// 既にグラフィックハンドルが作成されていたらソフトイメージハンドルのイメージをグラフィックハンドルに転送
				ReCreateGraphFromSoftImage(CameraSoftImageHandle, CameraGraphHandle);
			}

			// カメラの向きに応じて回転させつつ描画
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

		// カメラの状態と解像度を描画
		DrawFormatString(0, 0, GetColor(255, 255, 255), "CameraState : %s   Size : %d x %d",
			CameraState == 0 ? "準備中" : (CameraState == 1 ? "使用可能" : "使用不可能"),
			CameraImageSizeX, CameraImageSizeY
		);




		// 裏画面の内容を表画面に反映
		ScreenFlip();
	}

	// ＤＸライブラリの後始末
	DxLib_End();

	// ソフトの終了
	return 0;
}