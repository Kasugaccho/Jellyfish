#define ASLIB_INCLUDE_DL
#include <AsLib.hpp>

//ペットの管理
struct Pet {
	
	std::string name = u8"Jelly";
	//UI
	AnimeUI ui;
	//体力
	float hp = 100;
	//泳ぐ速さ
	Pos2F speed = { 0.6f,0.6f };
	//目的対象との距離
	float dis = 0.0f;

	Pet() = default;
	const Pet(const AnimeMainData* const add_tmd, const uint8_t add_alpha, const Pos4& add_pos4, const float hp_) :ui(add_tmd, add_alpha, add_pos4), hp(hp_) {}
	const Pet(const AnimeMainData* const add_tmd, const uint8_t add_alpha, const PosA4F& add_pos4, const float hp_) : ui(add_tmd, add_alpha, add_pos4), hp(hp_) {}
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
			this->hp += float(asRand32(100)) / 10.0f;
			if (this->hp > 100.0f) this->hp = 100.0f;
		}
		return *this;
	}
};

int32_t asMain()
{
	MainControl as(u8"Jellyfish", Pos2(1080, 1920), ColorRGB(0, 0, 0));
	//画像ファイル
	const AnimeMainData petA(32, asLoadTex(u8"P/Pet/pet.png", 8, 4));
	const AnimeMainData foodA(1, asLoadTex(u8"P/Pet/food.png", 1));
	//const AnimeMainData twitterA(1, asLoadTex(u8"P/SNS/twitter.png", 1));

	PosA4F p_a4(1000.0f, 300.0f, 100.0f, 100.0f);
	PosA4F def_a4(p_a4);
	float hp_def = 100.0f;

	asReadPos(u8"Save/jelly_0_size_0.dat", p_a4);
	asReadFloat(u8"Save/jelly_0_hp_0.dat", hp_def);

	//ペット
	Pet pet(&petA, 200, p_a4, hp_def);
	//餌
	std::vector<AnimeUI> food;

	FontMainData font(asMakeFont(asWindowSize().y / 16));

	//メインループ
	while (as.loop())
	{
		font.drawAt(u8"Name: %s", Pos2(asWindowSize().x / 2, asWindowSize().y / 16), ColorRGBA(100, 100, 200, 255), pet.name.c_str());
		font.drawAt(u8"Hp: %d", Pos2(asWindowSize().x / 2, asWindowSize().y * 2 / 16), ColorRGBA(100, 100, 200, 255), int32_t(pet.hp + 0.5f));
		font.drawAt(u8"Size: %f", Pos2(asWindowSize().x / 2, asWindowSize().y * 3 / 16), ColorRGBA(100, 100, 200, 255), pet.ui.PosF().w);

		pet.hp -= 0.000004f;
		if (pet.hp < 0.0f) { pet.ui.setPosF(def_a4); pet.name = "Jelly"; pet.hp = 100.0f; }
		pet.move(food).ui.fpsUpdate().addSizeF(0.000004f, 400.0f).addRota(0.0f).drawRF();
		for (size_t i = 0; i < food.size(); ++i) {
			//餌がウィンドウ外に出たら餌を消す
			if (food[i].addPosF(0.0f, 0.3f).addRota(0.005f).drawRF().isOutWindowF()) { food.erase(food.begin() + i); --i; }
		}
		//タッチされたら餌を出す
		if (as.isUp()) {
			food.emplace_back();
			food[food.size() - 1].setUI(&foodA, 100, PosA4F(float(as.touchPos().x), 0.0f, 30.0f, 30.0f));
		}

	}

	asWritePos(u8"Save/jelly_0_size_0.dat", pet.ui.PosF());
	asWriteFloat(u8"Save/jelly_0_hp_0.dat", pet.hp);
	return 0;
}