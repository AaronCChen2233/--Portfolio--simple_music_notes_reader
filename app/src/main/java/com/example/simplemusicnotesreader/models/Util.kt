package com.example.simplemusicnotesreader.models

import org.w3c.dom.Document
import java.io.InputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

fun parseXml(inputStream: InputStream, builder: DocumentBuilder = defaultDocumentBuilder()): Document {
    return builder.parse(inputStream)!!
}

fun defaultDocumentBuilder(builderFactory: DocumentBuilderFactory = defaultDocumentBuilderFactory()): DocumentBuilder {
    return builderFactory.newDocumentBuilder()
}

fun defaultDocumentBuilderFactory(): DocumentBuilderFactory {
    return DocumentBuilderFactory.newInstance()!!
}