package love.xiguajerry.nullhack.modules.impl.client

import love.xiguajerry.nullhack.utils.Displayable
import love.xiguajerry.nullhack.utils.ResourceHelper
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.graphics.texture.MipmapTexture
import javax.imageio.ImageIO

enum class AnimeType(override val displayName: CharSequence, val fileName: String, val color: ColorRGBA) : Displayable {
    HITORI_GOTOH("後藤ひとり", "HitoriGotoh", ColorRGBA(255, 76, 135)),
    IJICHI_NIJIKA("伊地知虹夏", "IjichiNijika", ColorRGBA(244, 210, 98)),
    YAMADA_RYO("山田リョウ", "YamadaRyō", ColorRGBA(58, 99, 167)),
    KITA_IKUYO("喜多郁代", "KitaIkuyo", ColorRGBA(221, 80, 89)),
    ISERI_NINA("井芹仁菜", "IseriNina", ColorRGBA(217, 14, 44)),
    KAWARAGI_MOMOKA("河原木桃香", "KawaragiMomoka", ColorRGBA(133, 201, 220)),
    AWA_SUBARU("安和すばる", "AwaSubaru", ColorRGBA(118, 189, 83)),
    RUPA("ルパ", "Rupa", ColorRGBA(238, 218, 1)),
    EBIZUKA_TOMO("海老塚智", "EbizukaTomo", ColorRGBA(227, 77, 141)),
    LYCORIS_PARTNER("リコリス・リコイル", "LycorisPartner", ColorRGBA(213, 60, 69)),
    NISHIKIGI_CHISATO("錦木千束", "NishikigiChisato", ColorRGBA(213, 60, 69)),
    NISHIKIGI_CHISATO2("錦木千束 (2)", "NishikigiChisato2", ColorRGBA(213, 60, 69)),
    INOE_TAKINA("井ノ上たきな", "InoeTakina", ColorRGBA(45, 51, 69)),
    AKIYAMA_MIO("秋山澪", "AkiyamaMio", ColorRGBA(43, 52, 67)),
    NAKANO_AZUSA("中野梓", "NakanoAzusa", ColorRGBA(43, 52, 67)),
    AZUMA_SEREN("あずませれん", "AzumaSeren", ColorRGBA(113, 139, 175)),
    MASHIRO_KANON("眞白かのん", "MashiroKanon", ColorRGBA(252, 218, 231)),
    ACE_TAFFY("永雏塔菲", "AceTaffy", ColorRGBA(233, 193, 202));

    val texture = MipmapTexture(
        ImageIO.read(ResourceHelper.getResourceStream("/assets/nullhack/background/$fileName.png")),
    )
}