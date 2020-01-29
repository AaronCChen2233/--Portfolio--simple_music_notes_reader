package com.example.simplemusicnotesreader.models

import com.example.simplemusicnotesreader.enums.Tie
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.lang.Exception


fun musicXmlReader(doc: Document): musicSheet {
    val barList = doc.getElementsByTagName("measure")
    val bars = getBarsDatas(barList)
    var title = ""

    /**Some sheet doesn't have title*/
    val credit = doc.getElementsByTagName("credit")
    if (credit.length > 0) {
        title = getNodeValue("credit-words", credit.item(0) as Element)
        title = title.replace("\t", "")
        title = title.replace("\n", "")
    }

    var barWidth = 300
    var barHeight = 10

    return musicSheet(title, barWidth, barHeight, bars)

}

fun getBarsDatas(docs: NodeList): ArrayList<barData> {
    var division = 0
    var speed = 0
    var barTime = 0L
    var repeatStartIndex = 0
    var repeatEndIndex = 0
    var timeSignation = ""
    var keySignation = ""
    val bars = ArrayList<barData>()
    for (i in 0..docs.length - 1) {
        val measure = docs.item(i)

        /**attributes part*/
        val barattr = (measure as Element).getElementsByTagName("attributes")
        if (barattr.length != 0) {
            val newDivision = getDivisions(barattr)
            division = if (newDivision != 0) newDivision else division
            timeSignation = getTimeSignature(barattr)
            keySignation = getkeySignature(barattr)

            val tempspeed = getSpeed(measure)
            speed = if (tempspeed != "") tempspeed.toInt() else speed
            barTime =
                (((60F / speed) * (timeSignation.get(0).toString().toFloat() * 1000))).toLong()

        } else {
            timeSignation = ""
        }

        val notes = getNotes(measure)

        /**Now for test use fixed width and height after test should use phone screen size*/
        val bar = barData(timeSignation, keySignation, notes, getTies(notes), barTime)
        bars.add(bar)

        /**If have repeat add to bars*/
        val repeat = (measure as Element).getElementsByTagName("repeat")
        if (repeat.length > 0) {
            val tieString = repeat.item(0).attributes.getNamedItem("direction").nodeValue
            if (tieString == "forward") {
                repeatStartIndex = i
            } else if (tieString == "backward") {
                repeatEndIndex = i
                bars.addAll(bars.subList(repeatStartIndex, repeatEndIndex))
            }
        }

    }
    return bars
}

fun getDivisions(barattr: NodeList): Int {
    var divisions = getNodeValue("divisions", barattr.item(0) as Element)
    return if (divisions != "") divisions.toInt() else 0
}

fun getTimeSignature(barattr: NodeList): String {
    val best = getNodeValue("beats", barattr.item(0) as Element)
    val beatType = getNodeValue("beat-type", barattr.item(0) as Element)
    if (best == "" || beatType == "") {
        return ""
    }
    return "$best/$beatType"
}

fun getkeySignature(barattr: NodeList) =
    majorCorverter(getNodeValue("fifths", barattr.item(0) as Element))

fun getSpeed(bar: Node) = getNodeValue("per-minute", bar as Element)

fun getTies(notes: ArrayList<note>): ArrayList<tie> {
    var ties = ArrayList<tie>()
    var startIndex = 0
    var stopIndex = 0

    for (i in 0..notes.size - 1) {
        when (notes.get(i).tie) {
            Tie.Start ->
                startIndex = i

            Tie.Stop -> {
                stopIndex = i
                ties.add(tie(startIndex, stopIndex))
            }

            Tie.Both -> {
                stopIndex = i
                ties.add(tie(startIndex, stopIndex))
                startIndex = i
            }
        }
    }

    return ties
}

fun getNotes(measure: Node): ArrayList<note> {

    val notes: ArrayList<note> = ArrayList<note>()

    val nList = (measure as Element).getElementsByTagName("note")
    for (i in 0..nList.length - 1) {
        try {
            val note = nList.item(i)
            val haveDot = (note as Element).getElementsByTagName("dot").length > 0
            val isRest = (note as Element).getElementsByTagName("rest").length > 0
            var key = ""
            var tie = Tie.Non
            if (isRest) {
                key = "C/5"
            } else {
                val pitch = (note as Element).getElementsByTagName("pitch").item(0) as Element
                val step = getNodeValue("step", pitch)
                val octave = getNodeValue("octave", pitch)
                key = "$step/$octave"

                /**Tie part*/
                val tieNode = (note as Element).getElementsByTagName("tie")
                if (tieNode.length > 1) {
                    tie = Tie.Both
                } else if (tieNode.length == 1) {
                    val tieString = tieNode.item(0).attributes.getNamedItem("type").nodeValue
                    tie = if (tieString == "start") Tie.Start else Tie.Stop
                }
            }

            val type = durationCorverter(getNodeValue("type", note as Element), isRest)
            val accidental = accidentalCorverter(getNodeValue("accidental", note as Element))
            notes.add(note(key, type, accidental, tie, haveDot))

        } catch (e: Exception) {
            print(i.toString())
        }
    }
    return notes
}

fun accidentalCorverter(xmlaccidental: String): String {
    var corvertedAccidental = ""
    when (xmlaccidental) {
        "double-sharp" ->
            corvertedAccidental = "##"
        "sharp" ->
            corvertedAccidental = "#"
        "natural" ->
            corvertedAccidental = "n"
        "flat" ->
            corvertedAccidental = "b"
        "flat-flat" ->
            corvertedAccidental = "bb"
    }
    return corvertedAccidental
}

fun durationCorverter(xmlDuration: String, isRest: Boolean): String {
    var corvertedDuration = ""
    when (xmlDuration) {
        "32nd" ->
            corvertedDuration = "32"
        "16th" ->
            corvertedDuration = "16"
        "eighth" ->
            corvertedDuration = "8"
        "quarter" ->
            corvertedDuration = "4"
        "half" ->
            corvertedDuration = "2"
        "whole" ->
            corvertedDuration = "1"
    }

    if (isRest) {
        /**If is whole rest note didn't have type*/
        corvertedDuration = if (corvertedDuration == "") "1" else corvertedDuration
        corvertedDuration += "r"
    }

    return corvertedDuration
}

fun majorCorverter(key: String): String {
    /**Default said it is C major*/
    var majorString = "C"
    when (key) {
        "0" -> majorString = "C"
        "1" -> majorString = "G"
        "2" -> majorString = "D"
        "3" -> majorString = "A"
        "4" -> majorString = "E"
        "5" -> majorString = "B"
        "6" -> majorString = "F#"
        "7" -> majorString = "C#"

        "-1" -> majorString = "F"
        "-2" -> majorString = "Bb"
        "-3" -> majorString = "Eb"
        "-4" -> majorString = "Ab"
        "-5" -> majorString = "Db"
        "-6" -> majorString = "Gb"
        "-7" -> majorString = "Cb"
    }
    return majorString
}


