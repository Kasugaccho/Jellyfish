#define ASLIB_INCLUDE_DL
#include <AsLib.hpp>

//�y�b�g�̊Ǘ�
struct Pet {

	std::string name = u8"Jelly";
	//UI
	AnimeUI ui;
	//�̗�
	float hp = 100;
	//�j������
	Pos2F speed = { 0.6f,0.6f };
	//�ړI�ΏۂƂ̋���
	float dis = 0.0f;

	Pet() = default;
	Pet(const AnimeMainData* add_tmd, const uint8_t add_alpha, const Pos4& add_pos4, const float hp_) :ui(add_tmd, add_alpha, add_pos4), hp(hp_) {}
	Pet(const AnimeMainData* add_tmd, const uint8_t add_alpha, const PosA4F& add_pos4, const float hp_) : ui(add_tmd, add_alpha, add_pos4), hp(hp_) {}
	//const Pet& tmd(const AnimeMainData* const add_tmd) { ui. = add_tmd; }

	Pet& move(std::vector<AnimeUI>& food)
	{
		//�a�����邩���ׁA�����a����������ŏ������̉a�ֈړ�����
		size_t dis_id = 0;
		if (searchMin(food, this->ui.PosF(), dis_id, &this->dis) == -1) {
			this->ui.addPosF(speed.moveHypot(ui.Rota()));
			this->ui.setPosF(asWindowSizeF());
			return *this;
		}
		const Pos2F p(((food[dis_id].PosF().x < this->ui.PosF().x) ? this->speed.x*(-1) : this->speed.x), ((food[dis_id].PosF().y < this->ui.PosF().y) ? this->speed.y*(-1) : this->speed.y));
		//�a�̕����Ɍ������Ĉړ�����
		this->ui.setRota(atan2(p.x, -p.y), 0.025f);
		this->ui.addPosF(speed.moveHypot(ui.Rota()));
		this->ui.setPosF(asWindowSizeF());

		//�a��H�ׂ�
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
	MainControl as(u8"Jellyfish", Pos2(540,960), ColorRGB(0, 0, 0));
	//�摜�t�@�C��
	const AnimeMainData petA(32, asLoadTex(u8"P/Pet/pet.png", 8, 4));
	const AnimeMainData foodA(1, asLoadTex(u8"P/Pet/food.png", 1));
	//const AnimeMainData twitterA(1, asLoadTex(u8"P/SNS/twitter.png", 1));

	PosA4F p_a4(1000.0f, 300.0f, 100.0f, 100.0f);
	PosA4F def_a4(p_a4);
	float hp_def = 100.0f;
	time_t time_def;

	asReadPos(u8"jelly_0_size_0.dat", p_a4);
	asReadFloat(u8"jelly_0_hp_0.dat", hp_def);
	if (asReadTime(u8"time_0.dat", time_def) == -1) time_def = time(nullptr);
	const float time_float = float((time(nullptr) - time_def));
	hp_def -= time_float * 0.0002f;
	p_a4.w += time_float * 0.000004f;
	p_a4.h += time_float * 0.000004f;

	//�y�b�g
	Pet pet(&petA, 200, p_a4, hp_def);
	//�a
	std::vector<AnimeUI> food;

	FontMainData font(asMakeFont(asWindowSize().x / 16));

	//���C�����[�v
	while (as.loop())
	{
		font.drawAt(u8"Name: %s", Pos2(asWindowSize().x / 2, asWindowSize().x / 16), ColorRGBA(100, 100, 200, 255), pet.name.c_str());
		font.drawAt(u8"Hp: %d", Pos2(asWindowSize().x / 2, asWindowSize().x * 2 / 16), ColorRGBA(100, 100, 200, 255), int32_t(pet.hp + 0.5f));
		font.drawAt(u8"Size: %f", Pos2(asWindowSize().x / 2, asWindowSize().x * 3 / 16), ColorRGBA(100, 100, 200, 255), pet.ui.PosF().w);

		pet.hp -= 0.000004f;
		if (pet.hp < 0.0f) { pet.ui.setPosF(def_a4); pet.name = "Jelly"; pet.hp = 100.0f; }
		pet.move(food).ui.fpsUpdate().addSizeF(0.000004f, 2000.0f).addRota(0.0f).drawRF();
		for (size_t i = 0; i < food.size(); ++i) {
			//�a���E�B���h�E�O�ɏo����a������
			if (food[i].addPosF(0.0f, 0.3f).addRota(0.005f).drawRF().isOutWindowF()) { food.erase(food.begin() + i); --i; }
		}
		//�^�b�`���ꂽ��a���o��
		if (as.isUp()) {
			food.emplace_back();
			food[food.size() - 1].setUI(&foodA, 100, PosA4F(float(as.touchPos().x), 0.0f, 30.0f, 30.0f));
		}
	}

	asWritePos(u8"jelly_0_size_0.dat", pet.ui.PosF());
	asWriteFloat(u8"jelly_0_hp_0.dat", pet.hp);
	asWriteTime(u8"time_0.dat", time(nullptr));
	return 0;
}