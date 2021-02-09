package revision.world.blocks.power

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.struct.EnumSet
import mindustry.Vars
import mindustry.Vars.tilesize
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.world.Edges
import mindustry.world.blocks.power.PowerGenerator
import mindustry.world.meta.StatUnit

open class WindTurbine(name: String) : PowerGenerator(name) {

    var fanRegion: TextureRegion? = null
    var topRegion: TextureRegion? = null

    override fun load() {
        super.load()
        fanRegion = Core.atlas.find("$name-fan")
        topRegion = Core.atlas.find("$name-top")
    }

    init {
        flags = EnumSet.of()
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        Draw.color(Pal.placing)
        Lines.stroke(size.toFloat())
        Lines.square(x * Vars.tilesize + offset, y * Vars.tilesize + offset, (Vars.tilesize / 2f) * (size + 4).toFloat())
    }

    override fun setStats() {
        super.setStats()
        stats.remove(generationType)
        stats.add(generationType, powerProduction * 60f, StatUnit.powerSecond)
    }

    override fun icons(): Array<TextureRegion> {
        return arrayOf(region, fanRegion!!, topRegion!!)
    }

    inner class WindTurbineBuild : GeneratorBuild() {

        var time = 0f

        override fun updateTile() {
            val edges = Edges.getEdges(size) + Edges.getEdges(size + 2)
            val base = edges.size
            val occupied = edges.count {
                Vars.world.tile(tile.x + it.x, tile.y + it.y).solid()
            }
            productionEfficiency = if (enabled) 1f - occupied.toFloat() / base else 0f
            time += delta() * productionEfficiency
        }

        override fun drawCracks() {}

        override fun draw() {
            Draw.rect(region, x, y)
            super.drawCracks()
            Draw.rect(fanRegion, x, y, time)
            Draw.rect(topRegion, x, y)
        }

        override fun drawSelect() {
            drawPlace(tileX(), tileY(), 0, true)
        }
    }
}