package com.example.data.seed

import com.example.data.local.BookEntity
import com.example.data.local.ChapterEntity
import com.example.data.local.ReviewEntity

object UzbekistanDeliveryData {
    val regions = listOf(
        "Toshkent shahri",
        "Toshkent viloyati",
        "Samarqand viloyati",
        "Buxoro viloyati",
        "Farg'ona viloyati",
        "Andijon viloyati",
        "Namangan viloyati",
        "Qashqadaryo viloyati",
        "Surxondaryo viloyati",
        "Xorazm viloyati",
        "Navoiy viloyati",
        "Jizzax viloyati",
        "Sirdaryo viloyati",
        "Qoraqalpog'iston Respublikasi"
    )

    data class PickupPoint(
        val id: String,
        val title: String,
        val city: String,
        val address: String,
        val hours: String
    )

    val pickupPoints = listOf(
        PickupPoint("pp_chilonzor", "Kitobxon Chilonzor markazi", "Toshkent shahri", "Chilonzor 9-daha, Bunyodkor shoh ko'chasi, 14-uy", "09:00 – 21:00"),
        PickupPoint("pp_navoiy", "Navoiy ko'chasi Kitob Olami nuqtasi", "Toshkent shahri", "Alisher Navoiy ko'chasi, 42-uy (Chorsu yaqinida)", "09:00 – 20:00"),
        PickupPoint("pp_yunusobod", "Yunusobod Mega-Mutolaa punkti", "Toshkent shahri", "Amir Temur shoh ko'chasi, 129B-uy", "10:00 – 22:00"),
        PickupPoint("pp_samarkand", "Registon Kitobxon maskani", "Samarqand", "Universitet xiyoboni, 18-uy", "09:00 – 20:00"),
        PickupPoint("pp_bukhara", "Buxoro Ark mutolaa nuqtasi", "Buxoro", "Bahouddin Naqshband ko'chasi, 27-uy", "09:00 – 19:30"),
        PickupPoint("pp_fergana", "Farg'ona Ma'rifat markazi", "Farg'ona", "Al-Farg'oniy ko'chasi, 51-uy", "09:00 – 20:00"),
        PickupPoint("pp_andijan", "Bobur bog'i topshirish punkti", "Andijon", "Navoiy shoh ko'chasi, 33-uy", "09:00 – 20:00")
    )
}

object CatalogSeedBooks {

    const val CAT_UZBEK = "O'zbek adabiyoti"
    const val CAT_WORLD = "Jahon adabiyoti"
    const val CAT_SELF_DEV = "Shaxsiy rivojlanish"
    const val CAT_SCIENCE_KIDS = "Bolalar va ilmiy"

    fun initialBooks(): List<BookEntity> = listOf(
        BookEntity(
            id = "otkan_kunlar",
            title = "O'tkan kunlar",
            author = "Abdulla Qodiriy",
            category = CAT_UZBEK,
            synopsis = "O'zbek romanchiligining oltin ustuni hisoblangan ushbu asar XIX asr o'rtalaridagi Turkiston hayoti, insoniy or-nomus, sadoqat va chin muhabbat haqida so'zlaydi. Otabek va Kumushbibining dardli hamda go'zal qissasi har bir kitobxon qalbida o'chmas iz qoldiradi.",
            printPriceSom = 65_000L,
            ebookPriceSom = 0L,
            isFreeEbook = true,
            rating = 4.9f,
            reviewCount = 342,
            pageCount = 384,
            publishYear = 2024,
            language = "O'zbek (Lotin)",
            publisher = "Sharq Ziyosi Nashriyoti",
            coverArtKey = "otkan_kunlar",
            coverPaletteIndex = 0,
            isBestseller = true
        ),
        BookEntity(
            id = "atom_odatlar",
            title = "Atom odatlar",
            author = "Jeyms Klir",
            category = CAT_SELF_DEV,
            synopsis = "Har kuni atigi 1 foizga yaxshilanish yil yakunida sizni 37 barobar yuqori natijaga olib chiqadi. Ushbu amaliy qo'llanma kichik va izchil odatlarni shakllantirish, zararli odatlardan xalos bo'lish hamda kundalik tizim qurish sirlarini aniq misollar bilan ochib beradi.",
            printPriceSom = 85_000L,
            ebookPriceSom = 29_000L,
            isFreeEbook = false,
            rating = 4.9f,
            reviewCount = 518,
            pageCount = 320,
            publishYear = 2025,
            language = "O'zbek (Lotin)",
            publisher = "Asaxiy & Ma'rifat",
            coverArtKey = "atom_odatlar",
            coverPaletteIndex = 2,
            isBestseller = true
        ),
        BookEntity(
            id = "alximik",
            title = "Alximik",
            author = "Paulo Koelo",
            category = CAT_WORLD,
            synopsis = "Andalusiyalik yosh cho'pon Santyagoning Misr ehromlari sari qilgan safar qissasi. Inson o'z qalb ovoziga quloq tutishi, taqdir alomatlarini o'qiy olishi va o'z orzusi yo'lida qo'rquvni yengishi haqidagi falsafiy durdona.",
            printPriceSom = 68_000L,
            ebookPriceSom = 22_000L,
            isFreeEbook = false,
            rating = 4.8f,
            reviewCount = 289,
            pageCount = 240,
            publishYear = 2024,
            language = "O'zbek (Lotin)",
            publisher = "Yangi Asr Avlodi",
            coverArtKey = "alximik",
            coverPaletteIndex = 1,
            isBestseller = true
        ),
        BookEntity(
            id = "dunyoning_ishlari",
            title = "Dunyoning ishlari",
            author = "O'tkir Hoshimov",
            category = CAT_UZBEK,
            synopsis = "Ona mehri, bolalik xotiralari, o'zbek mahallasining samimiy odamlari va vijdon pokligi haqida ko'zga yosh keltiruvchi hikoyalar turkumi. O'qigan sari qalbingiz tozarib, yaqinlaringiz qadrini yanada chuqurroq his etasiz.",
            printPriceSom = 55_000L,
            ebookPriceSom = 0L,
            isFreeEbook = true,
            rating = 5.0f,
            reviewCount = 412,
            pageCount = 288,
            publishYear = 2024,
            language = "O'zbek (Lotin)",
            publisher = "Adabiyot Bo'stoni",
            coverArtKey = "clothbound_ruby",
            coverPaletteIndex = 0,
            isBestseller = true
        ),
        BookEntity(
            id = "yulduzli_tunlar",
            title = "Yulduzli tunlar",
            author = "Pirimqul Qodirov",
            category = CAT_UZBEK,
            synopsis = "Shoh va shoir Zahiriddin Muhammad Boburning murakkab yoshligi, Andijon va Samarqand taxti uchun kurashlar, yurt sog'inchi hamda buyuk 'Boburnoma' asarining dunyoga kelishi mahorat bilan tasvirlangan tarixiy roman.",
            printPriceSom = 78_000L,
            ebookPriceSom = 25_000L,
            isFreeEbook = false,
            rating = 4.9f,
            reviewCount = 276,
            pageCount = 496,
            publishYear = 2024,
            language = "O'zbek (Lotin)",
            publisher = "Meros Nashriyoti",
            coverArtKey = "clothbound_indigo",
            coverPaletteIndex = 1,
            isBestseller = true
        ),
        BookEntity(
            id = "kichik_shahzoda",
            title = "Kichik shahzoda",
            author = "Antuan de Sent-Ekzyuperi",
            category = CAT_SCIENCE_KIDS,
            synopsis = "Bolalar uchun ertak, kattalar uchun esa chuqur hayot falsafasi. 'Eng muhim narsalarni ko'z bilan ko'rib bo'lmaydi, ularni faqat qalb bilan his etish mumkin' degan hikmatni eslatuvchi o'lmas asar.",
            printPriceSom = 49_000L,
            ebookPriceSom = 0L,
            isFreeEbook = true,
            rating = 4.9f,
            reviewCount = 365,
            pageCount = 144,
            publishYear = 2025,
            language = "O'zbek (Lotin)",
            publisher = "Zukko Kitobxon",
            coverArtKey = "clothbound_moss",
            coverPaletteIndex = 2,
            isBestseller = false
        ),
        BookEntity(
            id = "shum_bola",
            title = "Shum bola",
            author = "G'afur G'ulom",
            category = CAT_UZBEK,
            synopsis = "Quвноq, topqir va sho'x Qoravoyning Toshkent ko'chalari, bozorlari va qishloqlaridagi ajoyib sarguzashtlari. Xalqona yumor va jonli til bilan yozilgan ushbu qissa har qanday yoshdagi o'quvchiga ko'tarinki kayfiyat ulashadi.",
            printPriceSom = 45_000L,
            ebookPriceSom = 0L,
            isFreeEbook = true,
            rating = 4.8f,
            reviewCount = 198,
            pageCount = 208,
            publishYear = 2024,
            language = "O'zbek (Lotin)",
            publisher = "Sharq Ziyosi Nashriyoti",
            coverArtKey = "clothbound_terracotta",
            coverPaletteIndex = 3,
            isBestseller = false
        ),
        BookEntity(
            id = "diqqat",
            title = "Diqqat (Chuqur mehnat)",
            author = "Kel Nyuport",
            category = CAT_SELF_DEV,
            synopsis = "Ijtimoiy tarmoqlar va tinimsiz xabarnomalar davrida diqqatni bir nuqtaga jamlash eng noyob qobiliyatga aylandi. Kitob sizga qisqa vaqt ichida murakkab bilimlarni o'zlashtirish va yuqori unumdorlikka erishish usullarini o'rgatadi.",
            printPriceSom = 79_000L,
            ebookPriceSom = 26_000L,
            isFreeEbook = false,
            rating = 4.8f,
            reviewCount = 214,
            pageCount = 304,
            publishYear = 2025,
            language = "O'zbek (Lotin)",
            publisher = "Taraqqiyot Print",
            coverArtKey = "clothbound_slate",
            coverPaletteIndex = 5,
            isBestseller = false
        ),
        BookEntity(
            id = "mehrobdan_chayon",
            title = "Mehrobdan chayon",
            author = "Abdulla Qodiriy",
            category = CAT_UZBEK,
            synopsis = "Ma'rifatli mirzo Anvar va oqila Ra'noning pokiza muhabbati hamda riyokorlik va adolatsizlikka qarshi ma'naviy kurashi haqidagi yorqin tarixiy roman. O'zbek tilining nafosatini his qilish uchun betakror manba.",
            printPriceSom = 59_000L,
            ebookPriceSom = 0L,
            isFreeEbook = true,
            rating = 4.9f,
            reviewCount = 231,
            pageCount = 336,
            publishYear = 2024,
            language = "O'zbek (Lotin)",
            publisher = "Adabiyot Bo'stoni",
            coverArtKey = "clothbound_plum",
            coverPaletteIndex = 4,
            isBestseller = false
        ),
        BookEntity(
            id = "sapiens",
            title = "Sapiens: Insoniyat tarixi",
            author = "Yuval Noy Xarari",
            category = CAT_SCIENCE_KIDS,
            synopsis = "Qadimgi ovchi-terimchilardan tortib sun'iy intellekt asrigacha insoniyat qanday qilib Yer yuzining yetakchi turiga aylandi? Til, pul, davlatlar va ilm-fan taraqqiyoti haqida keng qamrovli ilmiy-ommabop sayohat.",
            printPriceSom = 95_000L,
            ebookPriceSom = 32_000L,
            isFreeEbook = false,
            rating = 4.8f,
            reviewCount = 305,
            pageCount = 512,
            publishYear = 2025,
            language = "O'zbek (Lotin)",
            publisher = "Ilm Ziyo",
            coverArtKey = "clothbound_indigo",
            coverPaletteIndex = 1,
            isBestseller = true
        )
    )

    fun chaptersPart1(): List<ChapterEntity> = listOf(
        // 1. O'tkan kunlar (Free full e-book)
        ChapterEntity(
            bookId = "otkan_kunlar",
            chapterNumber = 1,
            title = "1-bob. Marg'ilon karvonsaroyi",
            estimatedMinutes = 8,
            isFreePreview = true,
            content = """1264-inchi hijriy, dalv oyining o'n yettinchisi, qishki kunlarning biri, quyosh botqan, tevarakdan shom azoni eshitiladir...

Marg'ilonning eng ko'rkam karvonsaroylaridan birining eshigi oldida savdo karvonining tuyalari cho'ktirilgan, xizmatchilar yuk tushirish bilan band edilar. Hujralardan birining to'rida yigirma to'rt yoshlardagi ko'rkam, keng yelkali, qora qosh va ma'noli ko'zli yigit — Toshkentning mashhur savdogar va ulamolaridan Yusufbek hojining o'g'li Otabek o'ychan holda choy ho'plab o'tirar edi.

Uning yonida keksa va sodiq xizmatkori Hasanali dasturxon tuzar ekan, yigitning ko'nglidagi o'ylarini uqqandek muloyim so'z qotdi:
— Bek aka, safarimiz uzoq bo'ldi, lekin Marg'ilonning havosi boshqacha-ya. Bahor nafasi erta kelgandek tuyuladi.

Otabek jilmayib deraza orqali saroy hovlisidagi keksa chinorga boqdi. U yurt kezib, Shom va Misr, Rus va Buxoro tartiblarini ko'rgan, elning ahvolini o'ylab kechalari bedor bo'ladigan fikrli yigit edi. Uning nazarida ilm va adolat bo'lmagan joyda elning ertasi qorong'i bo'lib qolar edi.

Shu oqshom karvonsaroyga Marg'ilonning mo'tabar kishilaridan Mirzakarim qutidor va Ziyo shohichi tashrif buyurdilar. Suhbat samimiy va ma'noli kechdi. Taqdir iplari aynan shu suhbatdan so'ng Otabek hayotini butkul yangi o'zanga burib yuborishini hali hech kim bilmas edi."""
        ),
        ChapterEntity(
            bookId = "otkan_kunlar",
            chapterNumber = 2,
            title = "2-bob. Ariq bo'yidagi uchrashuv",
            estimatedMinutes = 9,
            isFreePreview = true,
            content = """Mirzakarim qutidorning hovlisi keng, sarishta va gullarga burkangan edi. Hovli o'rtasidan o'tgan zilol ariq bo'yida keksa tut va anor daraxtlari soya tashlab turardi.

Otabek mehmonxonada dam olar ekan, tonggi namozdan so'ng tahorat yangilash uchun hovli etagidagi ariq bo'yiga tushdi. Tong yorishib kelar, qushlarning mayin sayrashi sukunatni bezar edi. Shu payt ichkari eshik ohista ochilib, qo'lida oftoba tutgan nozik qomatli qiz ko'rindi.

Uning qora majnuntol sochlari yelkasiga tushgan, oydin yuzidagi tabiiy iffat va aql nuri kishini beixtiyor mahliyo etar edi. Bu qutidorning yagona gavhari — Kumushbibi edi. Qiz kutilmagan mehmonni ko'rib bir lahza to'xtab qoldi, yuzlariga alvon qizillik yugurdi.

Ikki yoshning nigohi atigi bir necha soniya to'qnashdi. Ammo shu qisqa lahza ichida hech qanday so'z bilan ifodalab bo'lmas ruhiy yaqinlik va pokiza hayrat ikki qalbga muhrlandi. Otabek shu kungacha hech bir go'zallik qarshisida bunday hayajon va ehtirom tuyg'usini kechirmagan edi.

Hujraga qaytgan Otabek uzoq vaqt xayolini yig'olmay o'tirdi. Uning nazarida Marg'ilon osmoni yanada yorug', ariq suvining shildirashi esa qadimiy bir qo'shiqdek jaranglar edi."""
        ),
        ChapterEntity(
            bookId = "otkan_kunlar",
            chapterNumber = 3,
            title = "3-bob. Va'da va muhabbat ahdi",
            estimatedMinutes = 10,
            isFreePreview = true,
            content = """Hasanalining ziyrakligi va Ziyo shohichining vositachiligi bilan Mirzakarim qutidor xonadoniga sovchilik so'zi ochildi. Qutidor Yusufbek hoji xonadonining ilmu odobini azaldan hurmat qilgani sababli, yoshlarning taqdirini bog'lashga rozilik bildirdi.

To'y oqshomi Marg'ilon odatlariga ko'ra kamtarin, ammo nihoyatda fayzli o'tdi. Kumushbibi faqat husnda emas, balki xat-savod, she'riyat va odobda ham tengsiz ekani Otabekni yanada baxtiyor etdi. Ular Fuzuliy va Navoiy g'azallarini birgalikda mutolaa qilar, har bir bayt ustida uzoq suhbat qurardilar.

— Begim, — dedi bir oqshom Kumushbibi sham yorug'ida kitob sahifasini yopar ekan, — muhabbat faqat shodlik kunlaridagina emas, sinovli damlarda ham sodiq qolish bilan o'lchanadi, shunday emasmi?

Otabek Kumushning ma'sum ko'zlariga boqib qat'iyat bilan javob berdi:
— Sadoqat yigitning or-nomusidir, Kumush. Dunyo charxpalagi qanday aylanmasin, bu qalbda sizdan o'zga surat bo'lmas.

Biroq zamon notinch, fitnachilar esa har burchakda payt poylab yurar edi. Toshkentdan kelgan shoshilinch xabar va tuhmat to'rlari ikki vafodor qalbni og'ir ayriliq sinovlariga ro'para qildi."""
        ),
        ChapterEntity(
            bookId = "otkan_kunlar",
            chapterNumber = 4,
            title = "4-bob. Maktublar va sadoqat g'alabasi",
            estimatedMinutes = 9,
            isFreePreview = true,
            content = """Toshkent va Marg'ilon orasidagi uzoq yo'llar Otabek uchun tuganmas sog'inch yo'liga aylandi. Soxta maktub orqali uyushtirilgan fitna ikki xonadon o'rtasiga tushunmovchilik solgan bo'lsa-da, Otabek haqiqat tagiga yetmaguncha tinchimadi.

U kecha-yu kunduz izlanib, yovuz kimsalarning qilmishlarini fosh etdi. Yusufbek hoji esa yurt tinchligi va el birligi yo'lida xalq oldida o'zining mashhur nasihatini so'zladi:
'Birodarlar, bir-birimizning etagimizdan tortishni qo'yaylik! Ilmu ma'rifat va ittifoq bo'lmasa, bizni kelajak avlodlar aslo kechirmas!'

Yillar shamoli ko'p sinovlarni boshdan kechirdi. Kumushbibining Toshkentga kelishi, Oybodoq va O'zbek oyim bilan bo'lgan samimiy uchrashuvlar xonadonga qayta bahor olib kirdi. Pokiza niyat, sabr va vafodorlik har qanday qorong'ulikdan ustun ekanini hayot yana bir bor isbotladi.

Asar so'ngida kitobxon inson umri o'tkinchi bo'lsa-da, u qoldirgan ezgulik, mardlik va chin muhabbat asrlar osha barhayot yashashiga imon keltiradi."""
        ),

        // 2. Atom odatlar (Chapter 1 free preview, Chapters 2-4 E-book / Print unlock)
        ChapterEntity(
            bookId = "atom_odatlar",
            chapterNumber = 1,
            title = "1-bob. Kichik odatlarning hayratlanarli kuchi",
            estimatedMinutes = 7,
            isFreePreview = true,
            content = """Ko'pchilik katta muvaffaqiyat katta va keskin harakatlarni talab qiladi, deb o'ylaydi. Aslida esa hayotimizni tubdan o'zgartiradigan narsa — kundalik mayda qarorlar va kichik, ya'ni 'atom' odatlardir.

Tasavvur qiling: samolyot Los-Anjelesdan Nyu-York sari uchishga chog'landi. Agar uchuvchi parvoz boshida yo'nalishni atigi 3,5 darajaga janubga burib yuborsa, samolyot burnining siljishi deyarli sezilmaydi. Ammo Amerika qit'asini kesib o'tgach, siz Nyu-Yorkka emas, butunlay boshqa shaharga — Vashingtonga qo'nasiz!

Matematika ham buni tasdiqlaydi:
• Har kuni 1 foizga yaxshilansangiz: 1.01^365 = 37.78 (yil oxirida 37 barobar kuchliroq bo'lasiz).
• Har kuni 1 foizga yomonlashsangiz: 0.99^365 = 0.03 (deyarli nolga tushib qolasiz).

Maqsadlar sizga yo'nalish beradi, lekin natijaga sizni TIZIM olib boradi. G'oliblar ham, mag'lublar ham bir xil maqsad qo'yadilar. Ularning farqi — har kungi kichik odatlar tizimidadir. Bugunoq kuniga atigi 10 bet kitob o'qish odatini boshlang — bir yilda bu 3650 sahifa, ya'ni 12–15 ta mukammal kitob demakdir!"""
        ),
        ChapterEntity(
            bookId = "atom_odatlar",
            chapterNumber = 2,
            title = "2-bob. Odatlar qanday shakllanadi: 4 bosqichli halqa",
            estimatedMinutes = 8,
            isFreePreview = false,
            content = """Har qanday odat to'rtta ketma-ket bosqichdan iborat miya halqasi orqali ishlaydi:
1. Ishora (Signal) — miyani harakat boshlashga undovchi turtki.
2. Istak (Motivatsiya) — odat ortidagi holat o'zgarishiga bo'lgan xohish.
3. Javob (Harakat) — siz bajaradigan amaliy ish yoki fikr.
4. Mukofot (Qoniqish) — odatning yakuniy mevasi.

Yaxshi odatni oson va barqaror qilish uchun xulq-atvor o'zgarishining 4 qonunidan foydalaning:
• 1-qonun: Uni KO'RINARLI qiling. Masalan, kechqurun kitob o'qimoqchi bo'lsangiz, kitobni javonda emas, yostig'ingiz ustiga qo'yib qo'ying.
• 2-qonun: Uni JOZIBALI qiling. Yoqtirgan choyingizni damlashni mutolaa bilan bog'lang.
• 3-qonun: Uni OSON qiling. 'Ikki daqiqa qoidasi'ni qo'llang: har qanday yangi odatni boshlash ikki daqiqadan ko'p vaqt olmasin ('Bir bob o'qiyman' emas, 'Kitobni ochib bir sahifa o'qiyman').
• 4-qonun: Uni QONIQARLI qiling. O'qilgan boblarni belgilab boring — ko'zga ko'rinadigan o'sish miyaga zavq bag'ishlaydi."""
        ),
        ChapterEntity(
            bookId = "atom_odatlar",
            chapterNumber = 3,
            title = "3-bob. Muhit dizayni irodadan kuchliroq",
            estimatedMinutes = 8,
            isFreePreview = false,
            content = """Insonlar ko'pincha 'Menda iroda yetishmaydi' deb o'zlarini ayblaydilar. Biroq eng intizomli odamlar irodasini ko'p ishlatadiganlar emas, balki vasvasali vaziyatlarga kamroq tushadigan muhit yaratgan insonlardir!

Atrofingizdagi makonni shunday loyihalangki, to'g'ri qaror eng oson qarorga aylansin:
• Ish stolingizdan chalg'ituvchi buyumlarni uzoqlashtiring.
• Telefonni mutolaa yoki ijod vaqtida boshqa xonada qoldiring.
• Har bir xona yoki burchakni bitta aniq faoliyatga bog'lang: kreslo — mutolaa uchun, ish stoli — diqqatli mehnat uchun, yotoq — faqat tinch uyqu uchun.

Shuningdek, 'Odatlarni zanjir qilish' usulini qo'llang:
'Men [mavjud odat]ni bajarganimdan keyin, darhol [yangi odat]ni qilaman.'
Misol: 'Ertalab nonushta qilib bo'lgach, darhol 15 daqiqa kitob o'qiyman.' Mavjud odat yangi odat uchun tabiiy signal vazifasini o'taydi."""
        ),
        ChapterEntity(
            bookId = "atom_odatlar",
            chapterNumber = 4,
            title = "4-bob. Shaxsiyatga asoslangan odatlar va izchillik",
            estimatedMinutes = 9,
            isFreePreview = false,
            content = """Uzoq muddatli o'zgarishning eng chuqur siri — 'Nimaga erishmoqchiman?' degan savoldan 'Qanday insonga aylanmoqchiman?' degan savolga o'tishdir.

'Men 20 ta kitob o'qimoqchiman' degan maqsad natijaga qaratilgan. 'Men kitobxon insonman' degan qarash esa shaxsiyatga qaratilgan! O'zingizni kitobxon deb bilganingizda, bo'sh vaqt topilishi bilan kitob ochish majburiyat emas, tabiiy o'zligingizga aylanadi.

Eng muhim oltin qoida: HECH QACHON IKKI MARTA KETMA-KET O'TKAZIB YUBORMANG.
Bir kun odatni bajara olmaslik — insoniylik, bu sizning mehnatingizni yo'qqa chiqarmaydi. Ammo ketma-ket ikki kun o'tkazib yuborish — yangi salbiy odatning boshlanishidir. Hatto eng band kunda ham atigi 2 daqiqa ajratib zanjirni saqlab qoling. Izchillik mukammallikdan ming chandon ustundir!"""
        ),

        // 3. Alximik
        ChapterEntity(
            bookId = "alximik",
            chapterNumber = 1,
            title = "1-bob. Andalusiya yaylovlari va takroriy tush",
            estimatedMinutes = 7,
            isFreePreview = true,
            content = """Yigitning ismi Santyago edi. Quyosh ufqqa egila boshlaganda u qo'y podasini eski, tomi nuragan cherkov tomon boshlab keldi. Cherkov hovlisida ulkan anjir daraxti o'sar, uning shoxlari orasidan kechki yulduzlar miltillab ko'rinardi.

Santyago ota-onasining xohishiga ko'ra ruhoniy bo'lish uchun seminariyada o'qigan, lotin tilini va kitob mutolaasini o'rgangan edi. Ammo uning qalbida dunyoni kezish, noma'lum shaharlar va keng ufqlarni ko'rish ishtiyoqi tinchlik bermasdi. Shu bois u otasidan oq fotiha va uch tilla tanga olib, cho'ponlikni tanladi.

So'nggi haftalarda Santyago aynan shu anjir daraxti ostida uxlaganda bir xil tush ko'rar edi: tushida u Misr ehromlari etagida yashiringan xazinani topib olardi. Tarifa shahriga yetib borgach, u Salim podshohi Melxisedek — donishmand qariya bilan uchrashdi.

Qariya yigitga umr bo'yi yodda qoladigan haqiqatni aytdi:
— Inson biror narsani chin dildan orzu qilsa, butun Koinot shu orzuning ro'yobga chiqishiga ko'maklashadi. Bu sening O'z Taqdir Yo'lingdir."""
        ),
        ChapterEntity(
            bookId = "alximik",
            chapterNumber = 2,
            title = "2-bob. Billur do'koni saboqlari",
            estimatedMinutes = 8,
            isFreePreview = false,
            content = """Afrika qirg'og'idagi Tanjer shahriga o'tgan Santyago birinchi kundayoq bor pulidan ayrilib qoldi. Tushkunlikka tushish o'rniga u tepalikdagi billur idishlar do'koniga ishga kirdi.

Keksa billurfurush o'ttiz yildan beri muqaddas safarga borishni orzu qilar, biroq bu orzu amalga oshsa hayotining mazmuni tugab qolishidan cho'chib, bir joyda qotib yashardi. Santyago esa do'konga yangi g'oyalar olib kirdi: do'kon oldiga billur piyolalarda xushbo'y yalpizli choy tortiq qilishni yo'lga qo'ydi.

Savdo mislsiz darajada gullab-yashnadi. Bir yil ichida Santyago nafaqat oldingi qo'y podasidan ikki barobar ko'p mablag' yig'di, balki arab tilini, sabrni va 'Maktub' tushunchasini o'rgandi. Endi uning oldida ikki yo'l turardi: Andalusiyaga qaytib tinch hayot kechirish yoki sahroni kesib o'tib o'z tushi ortidan borish. Santyago jasoratni tanladi."""
        ),
        ChapterEntity(
            bookId = "alximik",
            chapterNumber = 3,
            title = "3-bob. Sahro karvoni va Al-Fayum vohasi",
            estimatedMinutes = 8,
            isFreePreview = false,
            content = """Bepoyon Sahroi Kabir uzra yuzlab tuyalardan iborat karvon ohista suzib borardi. Kunduzi quyosh olov purkar, tunda esa toshlar sovuqdan charsillardi. Karvonda Santyago qadimgi kimyo kitoblarini mutolaa qilib ketayotgan ingliz olimi bilan do'stlashdi.

Ingliz kitoblardan 'Buyuk Asar' va 'Falsafa toshi' sirini izlar, Santyago esa sahro shamoli, qumlar harakati va tuyakashning xotirjam hikmatlaridan olam tilini o'rganardi:
— Men o'tmishda ham, kelajakda ham yashamayman, — dedi tuyakash gulxan atrofida. — Men uchun faqat HOZIR bor. Agar butun diqqatingni hozirgi lahzaga qarata olsang, dunyodagi eng baxtli insonsan.

Al-Fayum vohasida Santyago quduq boshida Fotimani uchratdi va chin muhabbat hech qachon insonni O'z Taqdir Yo'lidan to'xtatib qolmasligini, aksincha qanot bag'ishlashini angladi. Ko'p o'tmay u vohadagi haqiqiy Alximik bilan yuzma-yuz ko'rishdi."""
        ),
        ChapterEntity(
            bookId = "alximik",
            chapterNumber = 4,
            title = "4-bob. Shamol bilan suhbat va haqiqiy xazina",
            estimatedMinutes = 9,
            isFreePreview = false,
            content = """Alximik hamrohligida ehromlar sari ketayotgan Santyago eng katta sinovga duch keldi: qabilalar lagerida u o'z qalb qo'rquvini yengib, sahro, shamol va quyosh bilan umumiy olam tilida so'zlashdi.

— Faqat bitta narsa orzuni ro'yobga chiqarishga to'sqinlik qiladi: bu — muvaffaqiyatsizlikdan qo'rqishdir, — dedi Alximik vidolashuv oldidan va oltin bo'lagini yigit bilan bo'lishdi.

Santyago nihoyat ulug'vor Misr ehromlari qarshisiga yetib bordi va tiz cho'kib quvonchdan ko'z yosh to'kdi. Qiziq taqdir o'yini bilan aynan shu yerda eshitgan bir so'z unga xazina qayerda ekanini ochib berdi: haqiqiy xazina u safar boshlagan eski Andalusiya cherkovidagi anjir daraxti ostida ko'milgan edi!

Biroq u Sahroi Kabirni bekorga kesib o'tmagandi — agar u bu safarga chiqmaganida, na Fotimani uchratar, na dunyo go'zalligini ko'rar, na o'z ichidagi qudratni kashf etar edi."""
        ),

        // 4. Dunyoning ishlari (Free full e-book)
        ChapterEntity(
            bookId = "dunyoning_ishlari",
            chapterNumber = 1,
            title = "1-bob. Oq, oydin kechalar",
            estimatedMinutes = 7,
            isFreePreview = true,
            content = """Bolalikda yoz kechalari hovlimizdagi supada osmonga termulib yotishni yaxshi ko'rar edim. Osmon to'la yulduz, xuddi kimdir baxmal dasturxonga son-sanoqsiz kumush tanga sochib yuborgandek miltillaydi.

Onam yonimda o'tirib ohista yelpig'ich silkitar, mayin ovozda o'tgan kunlardan, bobolarimizning halol mehnati va mehr-oqibati haqida hikoya qilar edilar. Onamning kaftlari ishdan biroz qabargan, ammo peshonamga tekkanida dunyodagi eng yumshoq va eng shifobaxsh malhamdek tuyular edi.

— Bolam, — derdilar onam yulduzlarga ishora qilib, — har bir odamning osmondagi yulduzi uning qilgan yaxshiligi bilan porlab turadi. Kimgadir bir piyola suv tutsang ham, bir og'iz shirin so'z aytsang ham, u hech qachon izsiz ketmaydi.

O'sha oq, oydin kechalar yillar o'tib ham ko'nglimning eng yorug' burchagini isitib turadi."""
        ),
        ChapterEntity(
            bookId = "dunyoning_ishlari",
            chapterNumber = 2,
            title = "2-bob. Gilam paypoq",
            estimatedMinutes = 8,
            isFreePreview = true,
            content = """Har yili kuz tushib, kunlar salqinlashi bilan men tomog'im og'rib yotib qolar edim. Shifokorlar dori yozar, ammo onamning o'z davosi bor edi: qalin jundan mehr bilan to'qilgan rang-barang gilam paypoq!

Talabalik yillarim, ish bilan band bo'lib yurgan paytlarimda ham onam qish kirishi bilan qo'llariga to'qish ignasini olar edilar. Ba'zan shoshilib: 'Oyi, hozir do'konda issiq paypoq to'lib yotibdi-ku, o'zingizni qiynamang' deb yuborar edim. Onam esa ranjimasdan kulimsirab: 'Do'konning paypog'ida onaning duosi bo'ladimi, bolam?' deb qo'yardilar.

Oradan yillar o'tdi. Bugun javonimda o'sha so'nggi marta to'qib berilgan gilam paypoq turibdi. Uni har gal qo'limga olganimda nafaqat oyoqlarim, balki muzlab qolayotgan yuragim ham bir zumda isib ketgandek bo'ladi."""
        ),
        ChapterEntity(
            bookId = "dunyoning_ishlari",
            chapterNumber = 3,
            title = "3-bob. Alla va mehr qadri",
            estimatedMinutes = 7,
            isFreePreview = true,
            content = """Dunyoda barcha xalqlarning tili turlicha, biroq onalar aytadigan allaning ohangi hayratlanarli darajada bir-biriga o'xshaydi. Chunki alla til bilan emas, yurak urishi bilan aytiladi.

Mahallamizda kamgap, ammo hammadan birinchi bo'lib to'y-ma'rakaga yetib boradigan Hoji buvi bo'lar edilar. Urush yillarida besh yetim bolani o'z bag'riga olib voyaga yetkazgan bu mo'tabar ayol biror marta ham minnat so'zini tilga olmagan. Onam u kishini ko'rganlarida doim o'rinlaridan turib salom berardilar.

Inson ulg'aygani sari ko'p kitob o'qiydi, katta lavozimlar ko'radi. Lekin eng buyuk akademiya — bu onaning ko'zlaridagi duo va sabr maktabi ekanini ba'zan kech tushunib yetarkan."""
        ),
        ChapterEntity(
            bookId = "dunyoning_ishlari",
            chapterNumber = 4,
            title = "4-bob. Iltijo",
            estimatedMinutes = 7,
            isFreePreview = true,
            content = """Odamzod qiziq: yoshligida tezroq ulg'ayishga shoshiladi, ulg'aygach esa ish, tashvish, ertangi rejalar deb eng yaqin insonlariga besh daqiqa vaqt ajratishni 'ertaga'ga qoldiradi.

Agar hayotingizda onangiz yonigizda bo'lsa, bugunoq qo'ng'iroq qiling, yo'lingizni burib bo'lsa-da duolarini oling. Qo'llariga issiq non tutqazing, yuzlaridagi har bir ajinni ko'zga surting. Chunki onaning borligi — inson ustidagi eng mustahkam osmon, eng xavfsiz panohdir.

Dunyoning ishlari hech qachon tugamaydi. Ammo mehr ko'rsatishga ulgurgan daqiqalarimizgina umrimizning haqiqiy xazinasiga aylanadi."""
        ),

        // 5. Yulduzli tunlar
        ChapterEntity(
            bookId = "yulduzli_tunlar",
            chapterNumber = 1,
            title = "1-bob. Andijon tongi va yosh shahzoda",
            estimatedMinutes = 8,
            isFreePreview = true,
            content = """Farg'ona vodiysining zumrad bog'lari ustidan bahor tongi otmoqda. O'n ikki yoshli Zahiriddin Muhammad Bobur Axsi qo'rg'onidan yetib kelgan qayg'uli xabardan so'ng kutilmaganda ulkan saltanat mas'uliyatini o'z zimmasiga olishga majbur bo'ldi.

Yosh bo'lishiga qaramay, Boburning qalbida ham temuriylarga xos shijoat, ham Alisher Navoiy she'riyatidan oziqlangan nozik ta'b uyg'unlashgan edi. U ustozlari va sodiq beklar — Qosimbek hamda Xo'ja Mavloniy Qozining maslahatlariga quloq tutib, el-yurtni talondan saqlash uchun kecha-yu kunduz ot ustida bo'ldi.

Tunda esa chodir chirog'i yorug'ida daftar ochib, ko'rgan-kechirgan har bir voqeani rostgo'ylik bilan qog'ozga tushira boshladi."""
        ),
        ChapterEntity(
            bookId = "yulduzli_tunlar",
            chapterNumber = 2,
            title = "2-bob. Samarqand orzusi va Ulug'bek rasadxonasi",
            estimatedMinutes = 9,
            isFreePreview = false,
            content = """Samarqandning moviy gumbazlari uzoqdan quyosh nurida jilvalanar ekan, Bobur hayratdan ot jilovini tortdi. Ko'hak tepaligidagi Mirzo Ulug'bek rasadxonasiga ko'tarilgan yosh hukmdor bobosining yulduzlar jadvali va usturlob asboblarini chuqur ehtirom bilan ko'zdan kechirdi.

— Qilich bilan olingan mulk o'tkinchi bo'lishi mumkin, ammo ilm va qalam bilan barpo etilgan ma'rifat ming yillar yashaydi! — dedi Bobur me'mor Fazliddin bilan suhbatda.

Alisher Navoiydan Hirotdan kelgan maktub esa yosh shoirning ruhiga mislsiz ilhom bag'ishladi. Bobur o'zining ilk turkiy g'azallarini bitib, ulug' ustozga javob noma yo'lladi."""
        ),
        ChapterEntity(
            bookId = "yulduzli_tunlar",
            chapterNumber = 3,
            title = "3-bob. Vatan sog'inchi va tog' oshuvlari",
            estimatedMinutes = 8,
            isFreePreview = false,
            content = """Taqdir shamollari shafqatsiz keldi. Samarqand qamali va og'ir janglardan so'ng Bobur kichik guruh sodiq yigitlari bilan qorli Hindukush dovonlari osha Kobul sari yo'l oldi.

Qahraton sovuq kechada askarlar bilan bir xandaqda qor kechib o'tirgan shoh o'ziga alohida issiq g'or taklif qilinganida rad etdi:
— Navkarlarim qorda sovqotib turganida, men issiq joyda jon saqlasam, mardlik va adolat qayerda qoladi?!

Shu mardlik va halollik uning atrofida eng sodiq insonlarni birlashtirdi. Ammo u qayerga bormasin, Andijon qovunlarining hidi va Farg'ona ariqlarining ovozi qalbidan bir lahza ham ketmadi."""
        ),
        ChapterEntity(
            bookId = "yulduzli_tunlar",
            chapterNumber = 4,
            title = "4-bob. Boburnoma — abadiyat bitigi",
            estimatedMinutes = 9,
            isFreePreview = false,
            content = """Agra bog'larida yulduzli osmon ostida keksa Bobur o'z umr daftarining so'nggi sahifalarini ko'zdan kechirardi. U Hindistonda adolatli boshqaruv o'rnatdi, bog'-rog'lar, karvonsaroylar va kutubxonalar barpo etdi.

Farzandi Humoyun og'ir xastalikka chalinganida ota mehri toju taxtdan ham ustun keldi — u o'z jonini farzandi sog'lig'i yo'lida fido qilishni so'rab duo qildi. Tohir va Robiya orqali 'Boburnoma' nusxalari Movarounnahr sari yo'l oldi.

Zahiriddin Muhammad Bobur qoldirgan haqqoniy tarix va lirik ruboiylar jahon adabiyoti xazinasining eng porloq yulduzlaridan biri bo'lib qoldi."""
        )
    )

    fun initialReviews(): List<ReviewEntity> = listOf(
        ReviewEntity(
            bookId = "otkan_kunlar",
            reviewerName = "Dilnoza Karimova",
            rating = 5,
            comment = "Ilovada o'qish juda qulay ekan, sahifa rangini qog'oz rejimiga qo'yib bir nafasda o'qib chiqdim. Bosma nusxasini ham sovg'aga buyurtma qildim!"
        ),
        ReviewEntity(
            bookId = "otkan_kunlar",
            reviewerName = "Javohir Tursunov",
            rating = 5,
            comment = "Qodiriy tilining jozibasi baribir boshqacha. Har bir o'zbek xonadonida bo'lishi shart bo'lgan kitob."
        ),
        ReviewEntity(
            bookId = "atom_odatlar",
            reviewerName = "Sardorbek Alimov",
            rating = 5,
            comment = "Toshkent bo'ylab tezkor kuryer 3 soatda yetkazib berdi! Click orqali to'ladim, elektron versiyasi ham darhol ochildi."
        ),
        ReviewEntity(
            bookId = "alximik",
            reviewerName = "Madina Yusupova",
            rating = 5,
            comment = "Tushkunlikka tushgan paytda insonga kuch va ilhom beradigan asar. Tarjimasi juda ravon."
        ),
        ReviewEntity(
            bookId = "dunyoning_ishlari",
            reviewerName = "Azizbek Rahmonov",
            rating = 5,
            comment = "Gilam paypoq bobini o'qiyotib ko'zimdan yosh chiqib ketdi. Onajonlarimiz sog' bo'lishsin!"
        )
    )
}
