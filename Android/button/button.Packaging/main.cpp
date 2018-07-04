#define ASLIB_INCLUDE_DL
#include "Option.hpp"

//メイン関数
int32_t AsMain()
{
	//管理クラスの宣言
	MC mc(u8"GacchoBreeding", asSP_FullScreenSize({ 1920,1080 }), BG_COLOR);
	mc.battery.make({ 700,200,200,300 });
	
	//読み込み
	sceneInit(mc);
	textureInit(mc);
	UI_Init(mc);

	//com.twitter.android
	//初期シーン
	mc.scene(LOGO_SCENE1);

	//メインループ
	while (mc.isLoop()) mc.scenePlay();
	return 0;
}