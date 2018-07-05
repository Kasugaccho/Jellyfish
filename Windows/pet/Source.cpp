#define ASLIB_INCLUDE_DL
#include <AsLib.hpp>

//�y�b�g�̊Ǘ�
struct Pet {
	//UI
	AnimeUI ui;
	//�̗�
	float hp = 100;
	//�j������
	Pos2F speed = { 0.6f,0.6f };
	//�ړI�ΏۂƂ̋���
	float dis = 0.0f;

	Pet() = default;
	const Pet(const AnimeMainData* const add_tmd, const uint8_t add_alpha, const Pos4& add_pos4, const float hp_) :ui(add_tmd, add_alpha, add_pos4), hp(hp_) {}
	const Pet(const AnimeMainData* const add_tmd, const uint8_t add_alpha, const PosA4F& add_pos4, const float hp_) : ui(add_tmd, add_alpha, add_pos4), hp(hp_) {}
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
			if (this->hp > 100.0f) this->hp += 5.0f;
		}
		return *this;
	}
};

int32_t asMain()
{
	MainControl as(u8"Jellyfish", Pos2(1920, 1080), ColorRGB(0, 0, 0));
	//�摜�t�@�C��
	const AnimeMainData petA(32, asLoadTex(u8"P/Pet/pet.png", 8, 4));
	const AnimeMainData foodA(1, asLoadTex(u8"P/Pet/food.png", 1));
	const AnimeMainData twitterA(1, asLoadTex(u8"P/SNS/twitter.png", 1));

	PosA4F p_a4(1000.0f, 300.0f, 100.0f, 100.0f);

	//�y�b�g
	Pet pet(&petA, 200, p_a4, 10.0f);
	//�a
	std::vector<AnimeUI> food;

	//���C�����[�v
	while (as.loop())
	{
		//bgA.draw(0,asWindowSize());
		pet.move(food).ui.fpsUpdate().addSizeF(0.0001f, 400.0f).addRota(0.0f).drawRF();
		for (size_t i = 0; i < food.size(); ++i) {
			//�a���E�B���h�E�O�ɏo����a������
			if (food[i].addPosF(0.0f, 0.3f).addRota(0.005f).drawRF().isOutWindowF()) { food.erase(food.begin() + i); --i; }
		}
		//�^�b�`���ꂽ��a���o��
		if (as.isUp()) {
			food.emplace_back();
			food[food.size() - 1].setUI(&foodA, 100, PosA4F(float(asRand32(asWindowSize().x)), 0.0f, 30.0f, 30.0f));
		}
	}
	return 0;
}