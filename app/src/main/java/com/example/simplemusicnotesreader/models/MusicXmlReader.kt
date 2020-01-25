package com.example.simplemusicnotesreader.models

import com.example.simplemusicnotesreader.enums.Tie
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.lang.Exception


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

fun getkeySignature(barattr: NodeList) = majorCorverter(getNodeValue("fifths", barattr.item(0) as Element))


fun getNotes(measure: Node, divisions: Int): ArrayList<note> {

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
            notes.add(note(key, type, tie, haveDot))

        } catch (e: Exception) {
            print(i.toString())
        }
    }
    return notes
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
        corvertedDuration += "r"
    }

    return corvertedDuration
}

fun majorCorverter(key: String): String {
    /**Default said it is C major*/
    var majorString = "C"
    when (key){
        "0"-> majorString = "C"
        "1"-> majorString = "G"
        "2"-> majorString = "D"
        "3"-> majorString = "A"
        "4"-> majorString = "E"
        "5"-> majorString = "B"
        "6"-> majorString = "#F"
        "7"-> majorString = "#C"

        "-1"-> majorString = "F"
        "-2"-> majorString = "bB"
        "-3"-> majorString = "bE"
        "-4"-> majorString = "bA"
        "-5"-> majorString = "bD"
        "-6"-> majorString = "bG"
        "-7"-> majorString = "bC"
    }
    return  majorString
}

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

fun xmldocListCorvertTobarDataList(docs: NodeList): ArrayList<barData> {
    var division = 0
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
        } else {
            timeSignation = ""
        }

        val notes = getNotes(measure, division)

        /**Now for test use fixed width and height after test should use phone screen size*/
        val bar = barData(timeSignation, keySignation, notes, getTies(notes), 300, 10, 60)
        bars.add(bar)
    }
    return bars
}

fun getNodeValue(tag: String, element: Element): String {
    val nodeList = element.getElementsByTagName(tag)
    val node = nodeList.item(0)
    if (node != null) {
        if (node.hasChildNodes()) {
            val child = node.getFirstChild()
            while (child != null) {
                if (child.getNodeType() == Node.TEXT_NODE) {
                    return child.getNodeValue()
                }
            }
        }
    }
    return ""
}
