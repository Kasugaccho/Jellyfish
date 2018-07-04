package com.gaccho1.Packaging;

import android.app.NativeActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import java.lang.Runnable;

public class gaccho1 extends NativeActivity
{
	NativeActivity AppActivity ;
	EditText EditView ;
	AlertDialog.Builder AlBuilder ;

	// 入力された文字列を保存するメンバー変数
	String InputString ;

	// 文字列の入力が終わったら 1 にするフラグ用変数
	int InputEnd ;

	// 文字列入力ダイアログを開始する
	public void StartInputStringDialog()
	{
		// NativeActivity の参照をメンバー変数に保存しておく
		AppActivity = this ;

		// UIスレッドで実行する処理を登録する
		runOnUiThread( new Runnable ()
		{
			// UIスレッドで呼ばれる関数
			@Override public void run()
			{
				// 文字列入力の View を作成
				EditView = new EditText( AppActivity ) ;

				// ダイアログを作成
				AlBuilder = new AlertDialog.Builder( AppActivity ) ;

				// ダイアログのタイトルを設定
				AlBuilder.setTitle( "テキスト入力ダイアログ" ) ;

				// ダイアログに文字列の View をセット
				AlBuilder.setView( EditView ) ;

				// ダイアログに OK ボタンを追加
				AlBuilder.setPositiveButton( "OK!!", new DialogInterface.OnClickListener()
				{
					// OK ボタンが押されたときに呼ばれる関数
					public void onClick( DialogInterface dialog, int whichButton )
					{
						// 入力された文字列をメンバー変数に保存
						InputString = EditView.getText().toString() ;

						// 入力されたかどうかのフラグを立てる
						InputEnd = 1 ;

						// タイトルバーとナビゲーションバーを非表示にする処理
						View decor = AppActivity.getWindow().getDecorView() ;
						decor.setSystemUiVisibility(
							View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
							View.SYSTEM_UI_FLAG_LOW_PROFILE |
							View.SYSTEM_UI_FLAG_FULLSCREEN |
							View.SYSTEM_UI_FLAG_IMMERSIVE |
							View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
					}
				});

				// ダイアログを表示
				AlBuilder.show() ;
			}
		}) ;
	}

}
