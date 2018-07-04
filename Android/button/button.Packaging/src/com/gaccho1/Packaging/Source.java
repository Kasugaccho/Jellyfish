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

public class (�v���W�F�N�g��) extends NativeActivity
{
	// �ܓx
	double mLatitude ;

	// �o�x
	double mLongitude ;

	// �ʒu���̏��( 0�F�g�p�\���`�F�b�N��   1�F�g�p�\   2�F�g�p�s�\ )
	int mGPSState ;

	// �g�p����f�o�r�v���o�C�_
	String mUseGPSProvider ;

	// �ʒu�}�l�[�W��
	LocationManager mLocationManager ;

	// �ʒu���擾�̌��������N�G�X�g����ۂɎg�p���鎯�ʔԍ�
	static final int PERMISSIONS_REQUEST_LOCATION = 1 ;

	// �ʒu�}�l�[�W���̏��������s��
	private void InitializeLocationManager()
	{
		// �ʒu�}�l�[�W���̎擾
		mLocationManager = ( LocationManager )getSystemService( this.LOCATION_SERVICE ) ;

		// �g�p����f�o�r�v���o�C�_�̎擾

		// �[���̂f�o�r�̈ʒu��񂪎擾�ł���ꍇ�͒[���̂f�o�r���g�p
 		Location location = mLocationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
 		if( location != null )
 		{
 			mUseGPSProvider = LocationManager.GPS_PROVIDER;
 		}
 		else
 		{
			// �l�b�g���[�N����̈ʒu��񂪎擾�ł���ꍇ�̓l�b�g���[�N�̈ʒu�����g�p
 			location = mLocationManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );
 			if( location != null )
 			{
 				mUseGPSProvider = LocationManager.NETWORK_PROVIDER;
 			}
 			else
 			{
				// �ǂ��炩����ʒu��񂪎擾�ł��Ȃ��ꍇ�͂n�r�ɑI�����Ă��炤
				Criteria criteria = new Criteria() ;
				criteria.setAccuracy( Criteria.ACCURACY_FINE ) ;		// �ʒu���x�D��
				mUseGPSProvider = mLocationManager.getBestProvider( criteria, true ) ;
			}
		}

		// �ʒu���擾�̊J�n
		mLocationManager.requestLocationUpdates(
			mUseGPSProvider,
			3000, // �ʒu���X�V���s���Œ�X�V���ԊԊu( �P�ʁF�~���b )
			0,    // �ʒu���X�V���s���ŏ������Ԋu�i �P�ʁF���[�g�� �j
			new LocationListener() 
			{
				// �ʒu��񂪍X�V���ꂽ�Ƃ��ɌĂ΂��֐�
				@Override
				public void onLocationChanged( Location location )
				{
					// �ʒu���擾�����̃`�F�b�N
					if( ActivityCompat.checkSelfPermission(
							(�v���W�F�N�g��).this,
							Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
						ActivityCompat.checkSelfPermission(
							(�v���W�F�N�g��).this,
							Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
					{
						// �ʒu���擾�̌����������ꍇ�͉��������ɏI��
						return ;
					}

					// �����ɗ�����f�o�r���g�p�\�Ƃ������Ƃɂ���
					mGPSState = 1 ;

					// �o�x�ƈܓx���擾
 					mLatitude  = location.getLatitude() ;
 					mLongitude = location.getLongitude() ;

					// �ʒu��񂪍X�V���ꂽ�|��\��
					Toast.makeText( (�v���W�F�N�g��).this, "�ʒu��񂪍X�V����܂���", Toast.LENGTH_SHORT ).show() ;
				}

				// �g�p���Ă���f�o�r�v���o�C�_�������ɂȂ����ꍇ
				@Override
				public void onProviderDisabled( String provider )
				{
					Toast.makeText( (�v���W�F�N�g��).this, "�f�o�r�v���o�C�_�������ɂȂ�܂���", Toast.LENGTH_SHORT ).show() ;
				}

				// �g�p���Ă���f�o�r�v���o�C�_���L���ɂȂ����ꍇ
				@Override
				public void onProviderEnabled( String provider )
				{
					Toast.makeText( (�v���W�F�N�g��).this, "�f�o�r�v���o�C�_���L���ɂȂ�܂���", Toast.LENGTH_SHORT ).show() ;
				}

				// �g�p���Ă���f�o�r�v���o�C�_�̏�Ԃ��ω������ꍇ
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) 
				{
					Toast.makeText( (�v���W�F�N�g��).this, "�f�o�r�v���o�C�_�̏�Ԃ��ω����܂���", Toast.LENGTH_SHORT ).show() ;
				}
			}
		);
	}

	// �f�o�r�̏������J�n����
	public void StartGPS()
	{
		// UI�X���b�h�Ŏ��s���鏈����o�^����
		runOnUiThread( 
			new Runnable() 
			{
				// UI�X���b�h�ŌĂ΂��֐�
				@Override public void run()
				{
					// Android �̃o�[�W�����`�F�b�N
					if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M )
					{
						// Android 6.0�ȏ�̏ꍇ�̓A�v�����s���Ɉʒu���擾�̌��������邩���`�F�b�N����

						// �ʒu���擾�̌��������邩����
						if( checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED )
						{
							// �ʒu���擾�̌���������Έʒu�}�l�[�W����������
							Toast.makeText( (�v���W�F�N�g��).this, "���̃A�v���͈ʒu���擾�̌��������ɂ���܂�", Toast.LENGTH_SHORT ).show() ;
							InitializeLocationManager() ;
						}
						else
						{
							// �ʒu���擾�̌�����������Ό��������߂�_�C�A���O��\��
							requestPermissions(
								new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
								PERMISSIONS_REQUEST_LOCATION
							) ;
						}
					}
					else
					{
						// Android 6.0�����̏ꍇ�̓A�v�����s���ɂ͈ʒu���擾�̌�����������Ă���̂�
						// �������ňʒu�}�l�[�W���̏��������s��
						InitializeLocationManager() ;
					}
				}

				// �����̋������߂�_�C�A���O�ŋ����s�����I�����ꂽ��Ă΂��֐�
				public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults )
				{
					// �ʒu���擾�̌��������߂郊�N�G�X�g�ɑ΂��錋�ʂ̏ꍇ�̂ݏ������s��
					if( requestCode == PERMISSIONS_REQUEST_LOCATION )
					{
						// �����ꂽ�̂��ǂ����𔻒�
						if( grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED )
						{
							// �����ꂽ��ʒu�}�l�[�W����������
							Toast.makeText( (�v���W�F�N�g��).this, "�ʒu���擾��������܂���", Toast.LENGTH_SHORT ).show() ;
							InitializeLocationManager() ;
						}
						else
						{
							// ������Ȃ������炻�̎|��\������
							Toast.makeText( (�v���W�F�N�g��).this, "�ʒu���擾�����ۂ���܂���", Toast.LENGTH_SHORT ).show() ;

							// �f�o�r�g�p�s�̏�Ԃɂ���
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

	// �J�����f���̉𑜓x
	int mCameraImageSizeX;
	int mCameraImageSizeY;

	// �J�����f����YUV�C���[�W��Y�v�f���i�[����z���UV�v�f���i�[����z��
	byte mCameraYImage[];
	byte mCameraUVImage[];

	// �J�����f����YUV�t�H�[�}�b�g  1:NV21   2:NV12   3:I420
	int mImageFormat;

	// �J�����̉�]�p�x		0:0�x  1:90�x  2:180�x  3:270�x
	int mCameraRotation;

 	// �J�����̏��( 0:������   1:�C���[�W�擾�J�n   2:�G���[ )
 	int mCameraState;

	// �J�������擾�̌��������N�G�X�g����ۂɎg�p���鎯�ʔԍ�
	static final int PERMISSIONS_REQUEST_CAMERA = 2 ;

	// ByteBuffer �̎����A�h���X���擾����֐�
	private static long GetByteBufferAddress( final ByteBuffer buffer )
	{
		try {
			// Buffer �N���X�̃����o�[�ϐ� effectiveDirectAddress �փA�N�Z�X����ׂ� Field ���擾
			final java.lang.reflect.Field field = java.nio.Buffer.class.getDeclaredField( "effectiveDirectAddress" );

			// �l�ւ̃A�N�Z�X���\�ɂ���
			field.setAccessible( true );

			// �����A�h���X���擾���ĕԂ�
			return field.getLong( buffer );
		} catch( NoSuchFieldException e ) {
			e.printStackTrace();	// ��O������������R�[���X�^�b�N�����O�ɏo�͂���
		} catch( IllegalAccessException e ) {
			e.printStackTrace();	// ��O������������R�[���X�^�b�N�����O�ɏo�͂���
		}

		return 0;
	}

	// �J�����}�l�[�W���̏��������s��
	private void InitializeCameraManager()
	{
		// �X�}�[�g�t�H�����ǂ����𒲂ׂĂ���
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

		// �J�����}�l�[�W���̎擾
		mCameraManager = ( CameraManager )getSystemService( Context.CAMERA_SERVICE );

		try {
			// �L���ȃJ�����������������ǂ����̃t���O��|���Ă���
			boolean Flag = false;

			// ���ڂ���Ă���J������ID�z����擾
			String[] CameraIDs = mCameraManager.getCameraIdList();

			// �J������ID�̐��������[�v
			for( String cameraId : CameraIDs )
			{
				// �J�����̏����擾
				CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics( cameraId );

				// �w�ʃJ�����ȊO�̓L�����Z������
				if( characteristics.get( CameraCharacteristics.LENS_FACING ) == CameraCharacteristics.LENS_FACING_BACK )
				{
					StreamConfigurationMap map = characteristics.get( CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP );

					// �J�������Ή����Ă���T�C�Y�̔z����擾
					Size[] CameraImageSizes = map.getOutputSizes( SurfaceTexture.class );

					// �J�����̃T�C�Y 640x480 �� 320x240 ���g�p����
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

					// 640x480 ���L���ȏꍇ�� 640x480 ���A�����łȂ��ꍇ�� 320x240 ���g�p����
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
						// 640x480 �ɂ� 320x240 �ɂ��Ή����Ă��Ȃ�������L�����Z��
						continue;
					}

					// �J������ID��ۑ�
					mCameraId = cameraId;

					// �L���ȃJ���������������ǂ����̃t���O�𗧂Ă�
					Flag = true;
					break;
				}
			}
			
			// �L���ȃJ���������������炱���ŏI��
			if( Flag == false )
			{
				return;
			}
		} catch( CameraAccessException e ) {
			e.printStackTrace();	// ��O������������R�[���X�^�b�N�����O�ɏo�͂���
		}

		// �J������YUV�C���[�W��Y�v�f��ۑ�����z����쐬
		mCameraYImage  = new byte[ mCameraImageSizeX * mCameraImageSizeY ]; 

		// �J������YUV�C���[�W��UV�v�f��ۑ�����z����쐬
		mCameraUVImage = new byte[ ( mCameraImageSizeX / 2 ) * ( mCameraImageSizeY / 2 ) * 2 ]; 

		try {
			// �J�����ւ̐ڑ����J�n
			mCameraManager.openCamera(
				mCameraId,
				new StateCallback()
				{
					// �J�����ւ̐ڑ�������������Ă΂��֐�
					@Override
					public void onOpened( CameraDevice cameraDevice )
					{
						// �J�����̃C���[�W���擾����ׂ� ImageReader ���쐬
						mImageReader = ImageReader.newInstance( mCameraImageSizeX, mCameraImageSizeY, ImageFormat.YUV_420_888, 1 );

						// �J�����̃v���r���[�C���[�W�̎擾���N�G�X�g�̍쐬
						try {
							mCaptureRequestBuilder = cameraDevice.createCaptureRequest( CameraDevice.TEMPLATE_PREVIEW );
						} catch( CameraAccessException e ) {
							e.printStackTrace();	// ��O������������R�[���X�^�b�N�����O�ɏo�͂���
						}

						// �J�����̃v���r���[�C���[�W�̎擾���N�G�X�g��ɍ쐬���� ImageReader ��ǉ�
						mCaptureRequestBuilder.addTarget( mImageReader.getSurface() );

						// �J�����̃v���r���[�C���[�W�̎擾���J�n
						try {
							List< Surface > outputs = Arrays.asList( mImageReader.getSurface() );
							cameraDevice.createCaptureSession(
								outputs,
								new CameraCaptureSession.StateCallback()
								{
									// �C���[�W�̎擾���J�n���ꂽ��Ă΂��֐�
									@Override
									public void onConfigured( CameraCaptureSession session )
									{
										mCameraCaptureSession = session;

										// �I�[�g�t�H�[�J�X���[�h��ݒ�
										mCaptureRequestBuilder.set( CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE );

										// �t���b�V���̐ݒ�
										mCaptureRequestBuilder.set( CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH );

										// �C���[�W�̎擾���J��Ԃ��s�������̊J�n
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
											e.printStackTrace();	// ��O������������R�[���X�^�b�N�����O�ɏo�͂���
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
							e.printStackTrace();	// ��O������������R�[���X�^�b�N�����O�ɏo�͂���
						}

						// �����܂ŗ�����C���[�W�̎擾���J�n����Ă���
						mCameraState = 1;

						// ImageReader �̃C���[�W���X�V���ꂽ��Ă΂�鏈���̐ݒ�
						mImageReader.setOnImageAvailableListener(
							new OnImageAvailableListener()
							{
								// ImageReader �̃C���[�W���X�V���ꂽ�Ă΂��֐�
								@Override
								public void onImageAvailable( ImageReader reader )
								{
									// ���̃^�C�~���O�ŃJ�����̌������擾
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

									// ImageReader ���� Image ���擾
									Image image = mImageReader.acquireLatestImage();

									// �J�����̃C���[�W�̃t�H�[�}�b�g���擾
									if( mImageFormat == 0 )
									{
										// U�C���[�W�̃X�g���C�h��1byte�̏ꍇ�� I420�t�H�[�}�b�g
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
												// UV�̏��ԂŃC���[�W���i�[����Ă���ꍇ�� NV12�t�H�[�}�b�g
												mImageFormat = 2 ;
											}
											else
											{
												// VU�̏��ԂŃC���[�W���i�[����Ă���ꍇ�� NV21�t�H�[�}�b�g
												mImageFormat = 1 ;
											}
										}
									}
	
									// YUV�C���[�W���N���X�̔z��ɃR�s�[����
									{
										ByteBuffer ys = image.getPlanes()[ 0 ].getBuffer();
										ByteBuffer us = image.getPlanes()[ 1 ].getBuffer();
										ByteBuffer vs = image.getPlanes()[ 2 ].getBuffer();

										// Y�C���[�W�͑S�t�H�[�}�b�g����
										int i ;
										int loopNum = mCameraImageSizeX * mCameraImageSizeY ;
										for( i = 0 ; i < loopNum ; i ++ )
										{
											mCameraYImage[ i ] = ys.get( i );
										}

										// UV�C���[�W�̓t�H�[�}�b�g�ɂ���ď������قȂ�
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
	
									// Image �̉��
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
			e.printStackTrace();	// ��O������������R�[���X�^�b�N�����O�ɏo�͂���
		}
	}

	// �����̋������߂�_�C�A���O�ŋ����s�����I�����ꂽ��Ă΂��֐�
	public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults )
	{
		// �J�������擾�̌��������߂郊�N�G�X�g�ɑ΂��錋�ʂ̏ꍇ�̂ݏ������s��
		if( requestCode == PERMISSIONS_REQUEST_CAMERA )
		{
			// �����ꂽ�̂��ǂ����𔻒�
			if( grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED )
			{
				// �����ꂽ��J�����}�l�[�W����������
				Toast.makeText( (�v���W�F�N�g��).this, "�J�������擾��������܂���", Toast.LENGTH_SHORT ).show() ;
				InitializeCameraManager() ;
			}
			else
			{
				// ������Ȃ������炻�̎|��\������
				Toast.makeText( (�v���W�F�N�g��).this, "�J�������擾�����ۂ���܂���", Toast.LENGTH_SHORT ).show() ;

				// �J�����g�p�s�̏�Ԃɂ���
				mCameraState = 2 ;
			}
		}
	}

	// �J�����̏������J�n����
	public void StartCamera()
	{
		// UI�X���b�h�Ŏ��s���鏈����o�^����
		runOnUiThread( 
			new Runnable() 
			{
				// UI�X���b�h�ŌĂ΂��֐�
				@Override public void run()
				{
					// Android �̃o�[�W�����`�F�b�N
					if( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M )
					{
						// Android 6.0�ȏ�̏ꍇ�̓A�v�����s���Ɉʒu���擾�̌��������邩���`�F�b�N����

						// �J�������擾�̌��������邩����
						if( checkSelfPermission( Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED )
						{
							// �J�������擾�̌���������΃J�����}�l�[�W����������
							Toast.makeText( (�v���W�F�N�g��).this, "���̃A�v���̓J�������擾�̌��������ɂ���܂�", Toast.LENGTH_SHORT ).show() ;
							InitializeCameraManager() ;
						}
						else
						{
							// �J�������擾�̌�����������Ό��������߂�_�C�A���O��\��
							requestPermissions(
								new String[]{ Manifest.permission.CAMERA },
								PERMISSIONS_REQUEST_CAMERA
							) ;
						}
					}
					else
					{
						// Android 6.0�����̏ꍇ�̓A�v�����s���ɂ̓J�������擾�̌�����������Ă���̂�
						// �������ŃJ�����}�l�[�W���̏��������s��
						InitializeCameraManager() ;
					}
				}
			}
		) ;
	}
}