#define ASLIB_INCLUDE_DL
#include <AsLib.hpp>

//ペットの管理
struct Pet {
	//UI
	AnimeUI ui;
	//体力
	float hp = 100;
	//泳ぐ速さ
	Pos2F speed = { 0.6f,0.6f };
	//目的対象との距離
	float dis = 0.0f;

	Pet() = default;
	Pet(const AnimeMainData* const add_tmd, const uint8_t add_alpha, const Pos4& add_pos4, const float hp_) :ui(add_tmd, add_alpha, add_pos4), hp(hp_) {}
	Pet(const AnimeMainData* const add_tmd, const uint8_t add_alpha, const PosA4F& add_pos4, const float hp_) : ui(add_tmd, add_alpha, add_pos4), hp(hp_) {}
	//const Pet& tmd(const AnimeMainData* const add_tmd) { ui. = add_tmd; }

	Pet& move(std::vector<AnimeUI>& food)
	{
		//餌があるか調べ、もし餌があったら最小距離の餌へ移動する
		size_t dis_id = 0;
		if (searchMin(food, this->ui.PosF(), dis_id, &this->dis) == -1) {
			this->ui.addPosF(speed.moveHypot(ui.Rota()));
			this->ui.setPosF(asWindowSizeF());
			return *this;
		}
		const Pos2F p(((food[dis_id].PosF().x < this->ui.PosF().x) ? this->speed.x*(-1) : this->speed.x), ((food[dis_id].PosF().y < this->ui.PosF().y) ? this->speed.y*(-1) : this->speed.y));
		//餌の方向に向かって移動する
		this->ui.setRota(atan2(p.x, -p.y), 0.025f);
		this->ui.addPosF(speed.moveHypot(ui.Rota()));
		this->ui.setPosF(asWindowSizeF());

		//餌を食べる
		if (this->dis < ((ui.PosF().h > ui.PosF().w) ? ui.PosF().w / 6.0f : ui.PosF().h / 6.0f)) {
			food.erase(food.begin() + dis_id);
			if (this->hp > 100.0f) this->hp += 5.0f;
		}

		return *this;
	}
};

int32_t asMain()
{
	//(174,221,222)
	MainControl as(u8"Jellyfish", asSP_FullScreenSize(Pos2(1080,1920)), ColorRGB(0, 30, 67));
	//画像ファイル
	//AnimeMainData bgA(5, asLoadTex(u8"picture/bg.png", 1));
	AnimeMainData petA(32, asLoadTex(u8"picture/pet.png", 8, 4));
	AnimeMainData foodA(1, asLoadTex(u8"picture/food.png", 1));
	//AnimeMainData twitterA(1, asLoadTex(u8"picture/SNS/twitter.png", 1));
	
	PosA4F p_a4(600.0f, 100.0f, 100.0f, 100.0f);
	//asReadPos(u8"Save/pet0.txt", p_a4);

	//ペット
	Pet pet(&petA, 200, p_a4, 10.0f);
	//餌
	std::vector<AnimeUI> food;
	//food.resize(size_t(asRand8(10)));
	//for (AnimeUI& i : food) i.setUI(&foodA, 255, PosA4R(float(asRand32(asWindowSize().x)), 0.0f, 20.0f, 20.0f));

	//メインループ
	while (as.loop())
	{
		pet.move(food).ui.fpsUpdate().addSizeF(0.0001f, 400.0f).addRota(0.0f).drawRF();
		for (size_t i = 0; i < food.size(); ++i) {
			//餌がウィンドウ外に出たら餌を消す
			if (food[i].addPosF(0.0f, 0.3f).addRota(0.005f).drawRF().isOutWindowF()) { food.erase(food.begin() + i); --i; }
		}
		//タッチされたら餌を出す
		if (as.isUp()) {
			food.emplace_back();
			food[food.size() - 1].setUI(&foodA, 100, PosA4F(float(asRand32(asWindowSize().x)), 0.0f, 30.0f, 30.0f));
		}

		//if (twitterUI.draw().update().Up()) {
		//	t.makeUrl(u8"https://goo.gl/DMh1QF").hashtags(u8"test").related(u8"wanotaitei").text(u8"%E3%82%AF%E3%83%A9%E3%82%B2%E3%82%92%E8%82%B2%E6%88%90%E4%B8%AD%E2%80%A6%E2%80%A6").send();
		//}
	}
	asWritePos(u8"Save/pet0.txt", pet.ui.PosF());
	return 0;
}