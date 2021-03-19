package com.example.myapplication.utils

import com.example.myapplication.model.*

object MockedData {
    var mockedStudentList: ArrayList<Student>? = null
    var userImagesResponse: UserImagesResponse? = null
    var svgImagesList: ArrayList<SvgImage>? = null
    var svgImageDescriptionList: ArrayList<SvgImageDescription>? = null

    //-----------------------
    var svgIdList: ArrayList<Int>? = null

    fun initializeMockData(){
        svgImageDescriptionList = ArrayList()
        svgImagesList = ArrayList()
        svgImagesList?.add(SvgImage(0,"Obraz testowy nr 1", "Matematyka/Geometria/Zadanie 1","<svg width=\"1920\" height=\"960\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "    <script type=\"application/ecmascript\"> <![CDATA[\n" +
                "    function onClickEvent(evt) {\n" +
                "        Android.showDetail(evt.target.getAttribute(\"id\"));\n" +
                "    }\n" +
                "  ]]> </script>\n" +
                "\n" +
                "    <g>\n" +
                "        <title>Layer 1</title>\n" +
                "        <ellipse onclick=\"onClickEvent(evt)\" ry=\"400\" rx=\"400\" id=\"1\" cy=\"492.42192\" cx=\"1025.00022\" fill-opacity=\"null\" stroke-opacity=\"null\" stroke-width=\"15\" stroke=\"#000\" fill=\"none\"/>\n" +
                "        <path onclick=\"onClickEvent(evt)\" id=\"2\" d=\"m1023.12521,486.7969l350.62984,-143.04564l-334.72345,-233.75086l-379.02918,282.50104l363.12279,94.29546z\" fill-opacity=\"null\" stroke-opacity=\"null\" stroke-width=\"20\" stroke=\"#000\" fill=\"none\"/>\n" +
                "        <path onclick=\"onClickEvent(evt)\" stroke=\"#000\" transform=\"rotate(26 1030.0802001953125,423.7058715820312) \" id=\"3\" d=\"m887.05728,515.67797l165.58886,-32.23865l120.45706,-135.94999c-278.45138,-77.69111 -271.05355,156.58947 -286.04592,168.18864z\" fill-opacity=\"null\" stroke-opacity=\"null\" stroke-width=\"20\" fill=\"#000000\"/>\n" +
                "        <path onclick=\"onClickEvent(evt)\" stroke=\"#000\" transform=\"rotate(-16 1037.85107421875,162.60276794433597) \" id=\"4\" d=\"m963.58231,146.23026l90.49733,-32.6255l58.04018,71.7406c-153.65673,74.67152 -141.61678,-33.03626 -148.53751,-39.1151z\" fill-opacity=\"null\" stroke-opacity=\"null\" stroke-width=\"20\" fill=\"#000000\"/>\n" +
                "        <ellipse onclick=\"onClickEvent(evt)\" ry=\"25\" rx=\"25\" id=\"5\" cy=\"391.14447\" cx=\"663.83024\" stroke-width=\"1.5\" fill=\"#000\"/>\n" +
                "        <ellipse onclick=\"onClickEvent(evt)\" ry=\"25\" rx=\"25\" id=\"6\" cy=\"111.15119\" cx=\"1037.66743\" stroke-width=\"1.5\" fill=\"#000\"/>\n" +
                "        <ellipse onclick=\"onClickEvent(evt)\" ry=\"25\" rx=\"25\" id=\"7\" cy=\"344.22252\" cx=\"1367.65953\" stroke-width=\"1.5\" fill=\"#000\"/>\n" +
                "        <ellipse onclick=\"onClickEvent(evt)\" ry=\"25\" rx=\"25\" id=\"8\" cy=\"484.98838\" cx=\"1020.74476\" stroke-width=\"1.5\" fill=\"#000\"/>\n" +
                "    </g>\n" +
                "</svg>"))

        mockedStudentList = ArrayList()
        mockedStudentList?.add(Student(0, "Mateusz", "Kawulok"))
        mockedStudentList?.add(Student(1, "Mariusz", "Kawulok"))
        mockedStudentList?.add(Student(2, "Karol", "Kawulok"))
        mockedStudentList?.add(Student(3, "Adrian", "Kawulok"))
        mockedStudentList?.add(Student(4, "Adam", "Kawulok"))


        svgIdList = ArrayList()
        svgIdList?.add(0)
        svgIdList?.add(1)
        svgIdList?.add(2)
        svgIdList?.add(3)
        svgIdList?.add(4)
        svgIdList?.add(5)
        svgIdList?.add(6)
        userImagesResponse = UserImagesResponse(svgIdList)
    }
}