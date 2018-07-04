import android.app.NativeActivity;
import android.os.Bundle;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Size;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.view.Surface;

public class (プロジェクト名) extends NativeActivity
{
	// 緯度
	double mLatitude ;

	// 経度
	double mLongitude ;

	// 位置情報の状態( 0：使用可能かチェック中   1：使用可能   2：使用不可能 )
	int mGPSState ;

	// 使用するＧＰＳプロバイダ
	String mUseGPSProvider ;

	// 位置マネージャ
	LocationManager mLocationManager ;

	// 位置情報取得の権限をリクエストする際に使用する識別番号
	static final int PERMISSIONS_REQUEST_LOCATION = 1 ;

	// 位置マネージャの初期化を行う
	private void InitializeLocationManager()
	{
		// 位置マネージャの取得
		mLocationManager = ( LocationManager )getSystemService( this.LOCATION_SERVICE ) ;

		// 使用するＧＰＳプロバイダの取得

		// 端末のＧＰＳの位置情報が取得できる場合は端末のＧＰＳを使用
 		Location location = mLocationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
 		if( location != null )
 		{
 			mUseGPSProvider = LocationManager.GPS_PROVIDER;
 		}
 		else
 		{
			// ネットワークからの位置情報が取得できる場合はネットワークの位置情報を使用
 			location = mLocationManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );
 			if( location != null )
 			{
 				mUseGPSProvider = LocationManager.NETWORK_PROVIDER;
 			}
 			else
 			{
				// どちらからも位置情報が取得できない場合はＯＳに選択してもらう
				Criteria criteria = new Criteria() ;
				criteria.setAccuracy( Criteria.ACCURACY_FINE ) ;		// 位置精度優先
				mUseGPSProvider = mLocationManager.getBestProvider( criteria, true ) ;
			}
		}

		// 位置情報取得の開始
		mLocationManager.requestLocationUpdates(
			mUseGPSProvider,
			3000, // 位置情報更新を行う最低更新時間間隔( 単位：ミリ秒 )
			0,    // 位置情報更新を行う最小距離間隔（ 単位：メートル ）
			new LocationListener() 
			{
				// 位置情報が更新されたときに呼ばれる関数
				@Override
				public void onLocationChanged( Location location )
				{
					// 位置情報取得権限のチェック
					if( ActivityCompat.checkSelfPermission(
							(プロジェクト名).this,
							Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
						ActivityCompat.checkSelfPermission(
							(プロジェクト名).this,
							Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
					{
						// 位置情報取得の権限が無い場合は何もせずに終了
						return ;
					}

					// ここに来たらＧＰＳが使用可能ということにする
					mGPSState = 1 ;

					// 経度と緯度を取得
 					mLatitude  = location.getLatitude() ;
 					mLongitude = location.getLongitude() ;

					// 位置情報が更新された旨を表示
					Toast.makeText( (プロジェクト名).this, "位置情報が更新されました", Toast.LENGTH_SHORT ).show() ;
				}

				// 使用しているＧＰＳプロバイダが無効になった場合
				@Override
				public void onProviderDisabled( String provider )
				{
					Toast.makeText( (プロジェクト名).this, "ＧＰＳプロバイダが無効になりました", Toast.LENGTH_SHORT ).show() ;
				}

				// 使用しているＧＰＳプロバイダが有効になった場合
				@Override
				public void onProviderEnabled( String provider )
				{
					Toast.makeText( (プロジェクト名).this, "ＧＰＳプロバイダが有効になりました", Toast.LENGTH_SHORT ).show() ;
				}

				// 使用しているＧＰＳプロバイダの状態が変化した場合
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) 
				{
					Toast.makeText( (プロジェクト名).this, "ＧＰＳプロバイダの状態が変化しました", Toast.LENGTH_SHORT ).show() ;
				}
			}
		);
	}

	// ＧＰＳの処理を開始する
	public void StartGPS()
	{
		// UIスレッドで実行する処理を登録する
		runOnUiThread( 
			new Runnable() 
			{
				// UIスレッドで呼ばれる関数
				@Override public void run()
				{
					// Android のバージョンチェック
					if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M )
					{
						// Android 6.0以上の場合はアプリ実行中に位置情報取得の権限があるかをチェックする

						// 位置情報取得の権限があるか判定
						if( checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED )
						{
							// 位置情報取得の権限があれば位置マネージャを初期化
							Toast.makeText( (プロジェクト名).this, "このアプリは位置情報取得の権限が既にあります", Toast.LENGTH_SHORT ).show() ;
							InitializeLocationManager() ;
						}
						else
						{
							// 位置情報取得の権限が無ければ権限を求めるダイアログを表示
							requestPermissions(
								new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
								PERMISSIONS_REQUEST_LOCATION
							) ;
						}
					}
					else
					{
						// Android 6.0未満の場合はアプリ実行時には位置情報取得の権限が許可されているので
						// 無条件で位置マネージャの初期化を行う
						InitializeLocationManager() ;
					}
				}

				// 権限の許可を求めるダイアログで許可か不許可が選択されたら呼ばれる関数
				public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults )
				{
					// 位置情報取得の権限を求めるリクエストに対する結果の場合のみ処理を行う
					if( requestCode == PERMISSIONS_REQUEST_LOCATION )
					{
						// 許可されたのかどうかを判定
						if( grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED )
						{
							// 許可されたら位置マネージャを初期化
							Toast.makeText( (プロジェクト名).this, "位置情報取得が許可されました", Toast.LENGTH_SHORT ).show() ;
							InitializeLocationManager() ;
						}
						else
						{
							// 許可されなかったらその旨を表示する
							Toast.makeText( (プロジェクト名).this, "位置情報取得が拒否されました", Toast.LENGTH_SHORT ).show() ;

							// ＧＰＳ使用不可の状態にする
							mGPSState = 2 ;
						}
					}
				}
			}
		) ;
	}










	String mCameraId;
	boolean mIsPortraitDevice;
	CameraManager mCameraManager;
	CameraCaptureSession mCameraCaptureSession;
	CaptureRequest.Builder mCaptureRequestBuilder;
	ImageReader mImageReader;

	// カメラ映像の解像度
	int mCameraImageSizeX;
	int mCameraImageSizeY;

	// カメラ映像のYUVイメージのY要素を格納する配列とUV要素を格納する配列
	byte mCameraYImage[];
	byte mCameraUVImage[];

	// カメラ映像のYUVフォーマット  1:NV21   2:NV12   3:I420
	int mImageFormat;

	// カメラの回転角度		0:0度  1:90度  2:180度  3:270度
	int mCameraRotation;

 	// カメラの状態( 0:準備中   1:イメージ取得開始   2:エラー )
 	int mCameraState;

	// カメラ情報取得の権限をリクエストする際に使用する識別番号
	static final int PERMISSIONS_REQUEST_CAMERA = 2 ;

	// ByteBuffer の実効アドレスを取得する関数
	private static long GetByteBufferAddress( final ByteBuffer buffer )
	{
		try {
			// Buffer クラスのメンバー変数 effectiveDirectAddress へアクセスする為の Field を取得
			final java.lang.reflect.Field field = java.nio.Buffer.class.getDeclaredField( "effectiveDirectAddress" );

			// 値へのアクセスを可能にする
			field.setAccessible( true );

			// 実効アドレスを取得して返す
			return field.getLong( buffer );
		} catch( NoSuchFieldException e ) {
			e.printStackTrace();	// 例外が発生したらコールスタックをログに出力する
		} catch( IllegalAccessException e ) {
			e.printStackTrace();	// 例外が発生したらコールスタックをログに出力する
		}

		return 0;
	}

	// カメラマネージャの初期化を行う
	private void InitializeCameraManager()
	{
		// スマートフォンかどうかを調べておく
		boolean isPortraitApp = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		int orientation = getWindowManager().getDefaultDisplay().getRotation();
		if( isPortraitApp )
		{
			mIsPortraitDevice = ( orientation == Surface.ROTATION_0 || orientation ==  Surface.ROTATION_180 );
		}
		else
		{
			mIsPortraitDevice = ( orientation == Surface.ROTATION_90 || orientation ==  Surface.ROTATION_270 );
		}

		// カメラマネージャの取得
		mCameraManager = ( CameraManager )getSystemService( Context.CAMERA_SERVICE );

		try {
			// 有効なカメラが見つかったかどうかのフラグを倒しておく
			boolean Flag = false;

			// 搭載されているカメラのID配列を取得
			String[] CameraIDs = mCameraManager.getCameraIdList();

			// カメラのIDの数だけループ
			for( String cameraId : CameraIDs )
			{
				// カメラの情報を取得
				CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics( cameraId );

				// 背面カメラ以外はキャンセルする
				if( characteristics.get( CameraCharacteristics.LENS_FACING ) == CameraCharacteristics.LENS_FACING_BACK )
				{
					StreamConfigurationMap map = characteristics.get( CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP );

					// カメラが対応しているサイズの配列を取得
					Size[] CameraImageSizes = map.getOutputSizes( SurfaceTexture.class );

					// カメラのサイズ 640x480 か 320x240 を使用する
					boolean Valid640x480 = false;
					boolean Valid320x240 = false;
					for( Size cameraSize : CameraImageSizes )
					{
						if( cameraSize.getWidth() == 320 && cameraSize.getHeight() == 240 )
						{
							Valid320x240 = true;
						}
						else
						if( cameraSize.getWidth() == 640 && cameraSize.getHeight() == 480 )
						{
							Valid640x480 = true;
						}
					}

					// 640x480 が有効な場合は 640x480 を、そうでない場合は 320x240 を使用する
					if( Valid640x480 )
					{
						mCameraImageSizeX = 640;
						mCameraImageSizeY = 480;
					}
					else
					if( Valid320x240 )
					{
						mCameraImageSizeX = 320;
						mCameraImageSizeY = 240;
					}
					else
					{
						// 640x480 にも 320x240 にも対応していなかったらキャンセル
						continue;
					}

					// カメラのIDを保存
					mCameraId = cameraId;

					// 有効なカメラがあったかどうかのフラグを立てる
					Flag = true;
					break;
				}
			}
			
			// 有効なカメラが無かったらここで終了
			if( Flag == false )
			{
				return;
			}
		} catch( CameraAccessException e ) {
			e.printStackTrace();	// 例外が発生したらコールスタックをログに出力する
		}

		// カメラのYUVイメージのY要素を保存する配列を作成
		mCameraYImage  = new byte[ mCameraImageSizeX * mCameraImageSizeY ]; 

		// カメラのYUVイメージのUV要素を保存する配列を作成
		mCameraUVImage = new byte[ ( mCameraImageSizeX / 2 ) * ( mCameraImageSizeY / 2 ) * 2 ]; 

		try {
			// カメラへの接続を開始
			mCameraManager.openCamera(
				mCameraId,
				new StateCallback()
				{
					// カメラへの接続が完了したら呼ばれる関数
					@Override
					public void onOpened( CameraDevice cameraDevice )
					{
						// カメラのイメージを取得する為の ImageReader を作成
						mImageReader = ImageReader.newInstance( mCameraImageSizeX, mCameraImageSizeY, ImageFormat.YUV_420_888, 1 );

						// カメラのプレビューイメージの取得リクエストの作成
						try {
							mCaptureRequestBuilder = cameraDevice.createCaptureRequest( CameraDevice.TEMPLATE_PREVIEW );
						} catch( CameraAccessException e ) {
							e.printStackTrace();	// 例外が発生したらコールスタックをログに出力する
						}

						// カメラのプレビューイメージの取得リクエスト先に作成した ImageReader を追加
						mCaptureRequestBuilder.addTarget( mImageReader.getSurface() );

						// カメラのプレビューイメージの取得を開始
						try {
							List< Surface > outputs = Arrays.asList( mImageReader.getSurface() );
							cameraDevice.createCaptureSession(
								outputs,
								new CameraCaptureSession.StateCallback()
								{
									// イメージの取得が開始されたら呼ばれる関数
									@Override
									public void onConfigured( CameraCaptureSession session )
									{
										mCameraCaptureSession = session;

										// オートフォーカスモードを設定
										mCaptureRequestBuilder.set( CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE );

										// フラッシュの設定
										mCaptureRequestBuilder.set( CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH );

										// イメージの取得を繰り返し行う処理の開始
										try {
											mCameraCaptureSession.setRepeatingRequest(
												mCaptureRequestBuilder.build(),
												new CaptureCallback()
												{
													@Override
													public void onCaptureCompleted( CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result )
													{
														super.onCaptureCompleted( session, request, result );
													}
												},
												null
											);
										} catch( CameraAccessException e ){
											e.printStackTrace();	// 例外が発生したらコールスタックをログに出力する
										}
									}

									@Override
									public void onConfigureFailed( CameraCaptureSession session )
									{
									}
								},
								null
							);
						} catch( CameraAccessException e )
						{
							e.printStackTrace();	// 例外が発生したらコールスタックをログに出力する
						}

						// ここまで来たらイメージの取得が開始されている
						mCameraState = 1;

						// ImageReader のイメージが更新されたら呼ばれる処理の設定
						mImageReader.setOnImageAvailableListener(
							new OnImageAvailableListener()
							{
								// ImageReader のイメージが更新された呼ばれる関数
								@Override
								public void onImageAvailable( ImageReader reader )
								{
									// このタイミングでカメラの向きも取得
									{
										int orientation = getWindowManager().getDefaultDisplay().getRotation();
										switch( orientation )
										{
										case Surface.ROTATION_0:
											mCameraRotation = mIsPortraitDevice ? 3 : 0;
											break;

										case Surface.ROTATION_90:
											mCameraRotation = mIsPortraitDevice ? 0 : 1;
											break;

										case Surface.ROTATION_180:
											mCameraRotation = mIsPortraitDevice ? 1 : 2;
											break;

										case Surface.ROTATION_270:
											mCameraRotation = mIsPortraitDevice ? 2 : 3;
											break;
										}
									}

									// ImageReader から Image を取得
									Image image = mImageReader.acquireLatestImage();

									// カメラのイメージのフォーマットを取得
									if( mImageFormat == 0 )
									{
										// Uイメージのストライドが1byteの場合は I420フォーマット
										if( image.getPlanes()[ 1 ].getPixelStride() == 1 )
										{
											mImageFormat = 3;
										}
										else
										{
											ByteBuffer uBuffer = image.getPlanes()[ 1 ].getBuffer();
											ByteBuffer vBuffer = image.getPlanes()[ 2 ].getBuffer();
											if( GetByteBufferAddress( uBuffer ) < GetByteBufferAddress( vBuffer ) )
											{
												// UVの順番でイメージが格納されている場合は NV12フォーマット
												mImageFormat = 2 ;
											}
											else
											{
												// VUの順番でイメージが格納されている場合は NV21フォーマット
												mImageFormat = 1 ;
											}
										}
									}
	
									// YUVイメージをクラスの配列にコピーする
									{
										ByteBuffer ys = image.getPlanes()[ 0 ].getBuffer();
										ByteBuffer us = image.getPlanes()[ 1 ].getBuffer();
										ByteBuffer vs = image.getPlanes()[ 2 ].getBuffer();

										// Yイメージは全フォーマット共通
										int i ;
										int loopNum = mCameraImageSizeX * mCameraImageSizeY ;
										for( i = 0 ; i < loopNum ; i ++ )
										{
											mCameraYImage[ i ] = ys.get( i );
										}

										// UVイメージはフォーマットによって処理が異なる
										int j = 0 ;
										loopNum = ( mCameraImageSizeX / 2 ) * ( mCameraImageSizeY / 2 ) ;
										switch( mImageFormat )
										{
										case 1:	// I420
										case 2:	// NV12
											for( i = 0 ; i < loopNum ; i ++ )
											{
												mCameraUVImage[ j + 0 ] = us.get( j );
												mCameraUVImage[ j + 1 ] = vs.get( j );
												j += 2 ;
											}
											break;

										case 3:	// NV21
											for( i = 0 ; i < loopNum ; i ++ )
											{
												mCameraUVImage[ j + 0 ] = us.get( i );
												mCameraUVImage[ j + 1 ] = vs.get( i );
												j += 2 ;
											}
											break;
										}
									}
	
									// Image の解放
									image.close();
								}
							},
							null
						);
					}

					@Override
					public void onError(CameraDevice camera, int error)
					{
					}

					@Override
					public void onDisconnected(CameraDevice camera)
					{
					}
				},
				null
			);
		} catch( CameraAccessException e ) {
			e.printStackTrace();	// 例外が発生したらコールスタックをログに出力する
		}
	}

	// 権限の許可を求めるダイアログで許可か不許可が選択されたら呼ばれる関数
	public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults )
	{
		// カメラ情報取得の権限を求めるリクエストに対する結果の場合のみ処理を行う
		if( requestCode == PERMISSIONS_REQUEST_CAMERA )
		{
			// 許可されたのかどうかを判定
			if( grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED )
			{
				// 許可されたらカメラマネージャを初期化
				Toast.makeText( (プロジェクト名).this, "カメラ情報取得が許可されました", Toast.LENGTH_SHORT ).show() ;
				InitializeCameraManager() ;
			}
			else
			{
				// 許可されなかったらその旨を表示する
				Toast.makeText( (プロジェクト名).this, "カメラ情報取得が拒否されました", Toast.LENGTH_SHORT ).show() ;

				// カメラ使用不可の状態にする
				mCameraState = 2 ;
			}
		}
	}

	// カメラの処理を開始する
	public void StartCamera()
	{
		// UIスレッドで実行する処理を登録する
		runOnUiThread( 
			new Runnable() 
			{
				// UIスレッドで呼ばれる関数
				@Override public void run()
				{
					// Android のバージョンチェック
					if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M )
					{
						// Android 6.0以上の場合はアプリ実行中に位置情報取得の権限があるかをチェックする

						// カメラ情報取得の権限があるか判定
						if( checkSelfPermission( Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED )
						{
							// カメラ情報取得の権限があればカメラマネージャを初期化
							Toast.makeText( (プロジェクト名).this, "このアプリはカメラ情報取得の権限が既にあります", Toast.LENGTH_SHORT ).show() ;
							InitializeCameraManager() ;
						}
						else
						{
							// カメラ情報取得の権限が無ければ権限を求めるダイアログを表示
							requestPermissions(
								new String[]{ Manifest.permission.CAMERA },
								PERMISSIONS_REQUEST_CAMERA
							) ;
						}
					}
					else
					{
						// Android 6.0未満の場合はアプリ実行時にはカメラ情報取得の権限が許可されているので
						// 無条件でカメラマネージャの初期化を行う
						InitializeCameraManager() ;
					}
				}
			}
		) ;
	}
}