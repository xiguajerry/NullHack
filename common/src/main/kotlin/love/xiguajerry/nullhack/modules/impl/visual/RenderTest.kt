package love.xiguajerry.nullhack.modules.impl.visual

import love.xiguajerry.nullhack.RS
import love.xiguajerry.nullhack.event.api.nonNullHandler
import love.xiguajerry.nullhack.event.impl.render.Render2DEvent
import love.xiguajerry.nullhack.manager.managers.UnicodeFontManager
import love.xiguajerry.nullhack.modules.Category
import love.xiguajerry.nullhack.modules.Module
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects
import love.xiguajerry.nullhack.graphics.buffer.pmvbo.PMVBObjects.draw
import love.xiguajerry.nullhack.graphics.color.ColorRGBA
import love.xiguajerry.nullhack.graphics.font.TextComponent
import love.xiguajerry.nullhack.utils.math.vectors.HAlign
import love.xiguajerry.nullhack.utils.math.vectors.Vec2d
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP
import org.lwjgl.opengl.GL30

object RenderTest : Module("Render Test", category = Category.VISUAL) {
    private val string by setting("String", "Hello world!")

    private val text = TextComponent(font = UnicodeFontManager.MSYAHEI_9).apply {
        val rawText = """
「欢愉」是由星神「阿哈」执掌的命途。
「假面愚者」以一则寓言诉说着他们崇奉的神明是如何降生的——当欢愉之主攀上存在之树的高枝，祂窥见真空冰冷可憎，星辰机械运转，万
物意义让位于虚无。祂继续望去，直到目睹一名婴儿落地啼哭犹如受尽委屈，便忍不住纵声大笑。这清澈的笑声撕裂了冰冷死寂的宇宙，回荡诸界至今。
阿哈虽然没有其他星神那般壮阔伟大的战绩，但却常在无形中操纵着宇宙众生的运转，借由一圈微不足道的澜漪，在宇宙中演变成地动山摇的海啸。
根据符玄所作《易镜窥奥》中所写，在「帝皇」鲁珀特征服星海的时代，阿哈的信众在「哲学家联合」沦为无机生命的领土后于当地
再度掀起叛乱，并以名为「哲人鸩酒」的幽默悖论病毒侵染了征服者们的运算中枢，推翻了当地机械军团的暴政。
「悲悼伶人」是一群坚定的反欢愉主义者，他们极力否定欢愉的价值，搭乘跨越星星的贡多拉进行演出，在全宇宙为生命的不幸哀悼，
提倡禁欲与苦修，以求能够与阿哈对宇宙带来的甜美诱惑背道而驰。然而所谓欢愉乃是与悲伤对立的存在，悲喜是镌于灵魂这枚硬币
两面的花纹。于悲剧之美中，欢愉之神亦能窥见力量，许是出于对黑色幽默的喜爱，阿哈将自己的星神之力赐予了这群苦行者，并
饶有趣味地帮助他们跨越诸多星系，传播对欢愉的弃绝。
黑塔在「模拟宇宙」的开发日志中提到，为了捉弄「开拓」阿基维利和祂的无名客，阿哈曾假扮作一名普通人登上星穹列车，在列车上
潜伏了一年多的时间后抓住机会把半辆列车和一颗星球炸成了粉末。开发日志中还提到，曾经有一伙「泯灭帮」突发奇想试图刺杀星神，
他们选定了「虚无」Ⅸ作为目标，并得到了阿哈的赞助，因为祂觉得这个想法很有意思。最后他们下落不明。
据说，「欢愉」阿哈会随机给予凡人命途之力，并随着心情将其玩弄于股掌之中。在以利亚萨拉斯的记载中，有一次，阿哈把一只
诺布莱斯虫变成了自己的令使，给了它无比强大的智能，将命途的全部力量拱手交给一只大脑都没有发育完全的虫子，然后撒手而去，
只为试试能不能把诺布莱斯虫送进天才俱乐部。最后，这次实验失败了，失去星神眷顾的诺布莱斯虫瞬间就死去了，死状颇为凄惨。
据太卜司卜者绘星所述，在亚伯南安星系，人们受阿哈的神启，每八个标准月随机抽选出某位星系居民的注册编号，开奖的当天让那人变成大富翁。
寰宇蝗灾期间，一伙「假面愚者」将酒杯里的异样波纹认为是阿哈的神启，争执了三天三夜后，认为是阿哈在要求他们去帮助众神
对付「繁育」塔伊兹育罗斯，虽然尚不明确阿哈是否真的给了他们神启，但阿哈还是承认了这点，并为他们提供了十来种帮助众神的办法。
那一伙「假面愚者」盗走了「悲怆伶人」的贡多拉，载着他们以及一群藏在船底的无名客一起前往列神之战的战场，途中为了确认
一切不是阿哈的作弄，愚者们经历了几番内斗，最终贡多拉上载满了来自各个文明、各个命途的好心人，而愚者们却不见了。
尚不清楚这群凡人是否有对列神之战产生过一丝影响，但阿哈应该从他们身上收获了不少乐子。
"""
        rawText.split("\n").forEach { addLine(it) }
    }
    private val text2 = TextComponent().apply {
        val rawText = """
「言ったでしょう？残りの人生、わたくしに下さいと」
豊川祥子がメンバーを招き入れた
バンド・Ave Mujicaは、
ライブやメディア露出など、
商業的な成功を収めていた。

運命をともにすると誓った仲間も、
生まれ育った家も失った少女。
彼女は何のために他人の一生を背負い、
バンドを続けるのか。

過去も素顔も仮面で覆い隠し、
今宵も完璧な箱庭に降り立つ。
――これからご覧にいれますのは、
秘密を抱えた、彼女の話。
        """.trimIndent()
        rawText.split("\n").forEach { addLine(it) }
    }

    init {
        nonNullHandler<Render2DEvent> {
//            UnicodeFontManager.MSYAHEI_9_ARRAY.cache(false)
//            UnicodeFontManager.MSYAHEI_9_ARRAY.drawString(string, ColorRGBA.WHITE)
//            UnicodeFontManager.MSYAHEI_9.cache(false)
//            GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, UnicodeFontManager.SPARSE_ARRAYED_FONT.texture)
//            GL_TRIANGLE_STRIP.draw(PMVBObjects.VertexMode.Pos2fColorTexArray) {
//                vertex(1000f, 0f, 1f, -1f, 1, ColorRGBA.WHITE)
//                vertex(0f, 0f, -1f, -1f, 1, ColorRGBA.WHITE)
//                vertex(1000f, 1000f, 1f, 1f, 1, ColorRGBA.WHITE)
//                vertex(0f, 1000f, -1f, 1f, 1, ColorRGBA.WHITE)
//            }
            UnicodeFontManager.CURRENT_FONT.drawString("A quick brown fox jumps over a lazy dog.", ColorRGBA.WHITE)
            UnicodeFontManager.CURRENT_FONT.drawString(
"""「欢愉」是由星神「阿哈」执掌的命途。
「假面愚者」以一则寓言诉说着他们崇奉的神明是如何降生的——当欢愉之主攀上存在之树的高枝，祂窥见真空冰冷可憎，星辰机械运转，万
物意义让位于虚无。祂继续望去，直到目睹一名婴儿落地啼哭犹如受尽委屈，便忍不住纵声大笑。这清澈的笑声撕裂了冰冷死寂的宇宙，回荡诸界至今。
阿哈虽然没有其他星神那般壮阔伟大的战绩，但却常在无形中操纵着宇宙众生的运转，借由一圈微不足道的澜漪，在宇宙中演变成地动山摇的海啸。
根据符玄所作《易镜窥奥》中所写，在「帝皇」鲁珀特征服星海的时代，阿哈的信众在「哲学家联合」沦为无机生命的领土后于当地
再度掀起叛乱，并以名为「哲人鸩酒」的幽默悖论病毒侵染了征服者们的运算中枢，推翻了当地机械军团的暴政。
「悲悼伶人」是一群坚定的反欢愉主义者，他们极力否定欢愉的价值，搭乘跨越星星的贡多拉进行演出，在全宇宙为生命的不幸哀悼，
提倡禁欲与苦修，以求能够与阿哈对宇宙带来的甜美诱惑背道而驰。然而所谓欢愉乃是与悲伤对立的存在，悲喜是镌于灵魂这枚硬币
两面的花纹。于悲剧之美中，欢愉之神亦能窥见力量，许是出于对黑色幽默的喜爱，阿哈将自己的星神之力赐予了这群苦行者，并
饶有趣味地帮助他们跨越诸多星系，传播对欢愉的弃绝。
黑塔在「模拟宇宙」的开发日志中提到，为了捉弄「开拓」阿基维利和祂的无名客，阿哈曾假扮作一名普通人登上星穹列车，在列车上
潜伏了一年多的时间后抓住机会把半辆列车和一颗星球炸成了粉末。开发日志中还提到，曾经有一伙「泯灭帮」突发奇想试图刺杀星神，
他们选定了「虚无」Ⅸ作为目标，并得到了阿哈的赞助，因为祂觉得这个想法很有意思。最后他们下落不明。
据说，「欢愉」阿哈会随机给予凡人命途之力，并随着心情将其玩弄于股掌之中。在以利亚萨拉斯的记载中，有一次，阿哈把一只
诺布莱斯虫变成了自己的令使，给了它无比强大的智能，将命途的全部力量拱手交给一只大脑都没有发育完全的虫子，然后撒手而去，
只为试试能不能把诺布莱斯虫送进天才俱乐部。最后，这次实验失败了，失去星神眷顾的诺布莱斯虫瞬间就死去了，死状颇为凄惨。
据太卜司卜者绘星所述，在亚伯南安星系，人们受阿哈的神启，每八个标准月随机抽选出某位星系居民的注册编号，开奖的当天让那人变成大富翁。
寰宇蝗灾期间，一伙「假面愚者」将酒杯里的异样波纹认为是阿哈的神启，争执了三天三夜后，认为是阿哈在要求他们去帮助众神
对付「繁育」塔伊兹育罗斯，虽然尚不明确阿哈是否真的给了他们神启，但阿哈还是承认了这点，并为他们提供了十来种帮助众神的办法。
那一伙「假面愚者」盗走了「悲怆伶人」的贡多拉，载着他们以及一群藏在船底的无名客一起前往列神之战的战场，途中为了确认
一切不是阿哈的作弄，愚者们经历了几番内斗，最终贡多拉上载满了来自各个文明、各个命途的好心人，而愚者们却不见了。
尚不清楚这群凡人是否有对列神之战产生过一丝影响，但阿哈应该从他们身上收获了不少乐子。
""", ColorRGBA.WHITE)
            val width = text2.getWidth()
//            text.draw(Vec2d(10.0, 10.0))
            text2.draw(Vec2d(RS.scaledWidth - width / 2 - 10f, 10.0), horizontalAlign = HAlign.CENTER)
        }
    }
}