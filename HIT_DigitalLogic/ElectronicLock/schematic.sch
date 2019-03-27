# File saved with Nlview 6.6.5b  2016-09-06 bk=1.3687 VDI=39 GEI=35 GUI=JA:1.6
# 
# non-default properties - (restore without -noprops)
property attrcolor #000000
property attrfontsize 8
property autobundle 1
property backgroundcolor #ffffff
property boxcolor0 #000000
property boxcolor1 #000000
property boxcolor2 #000000
property boxinstcolor #000000
property boxpincolor #000000
property buscolor #008000
property closeenough 5
property createnetattrdsp 2048
property decorate 1
property elidetext 40
property fillcolor1 #ffffcc
property fillcolor2 #dfebf8
property fillcolor3 #f0f0f0
property gatecellname 2
property instattrmax 30
property instdrag 15
property instorder 1
property marksize 12
property maxfontsize 12
property maxzoom 5
property netcolor #19b400
property objecthighlight0 #ff00ff
property objecthighlight1 #ffff00
property objecthighlight2 #00ff00
property objecthighlight3 #ff6666
property objecthighlight4 #0000ff
property objecthighlight5 #ffc800
property objecthighlight7 #00ffff
property objecthighlight8 #ff00ff
property objecthighlight9 #ccccff
property objecthighlight10 #0ead00
property objecthighlight11 #cefc00
property objecthighlight12 #9e2dbe
property objecthighlight13 #ba6a29
property objecthighlight14 #fc0188
property objecthighlight15 #02f990
property objecthighlight16 #f1b0fb
property objecthighlight17 #fec004
property objecthighlight18 #149bff
property objecthighlight19 #eb591b
property overlapcolor #19b400
property pbuscolor #000000
property pbusnamecolor #000000
property pinattrmax 20
property pinorder 2
property pinpermute 0
property portcolor #000000
property portnamecolor #000000
property ripindexfontsize 8
property rippercolor #000000
property rubberbandcolor #000000
property rubberbandfontsize 12
property selectattr 0
property selectionappearance 2
property selectioncolor #0000ff
property sheetheight 44
property sheetwidth 68
property showmarks 1
property shownetname 0
property showpagenumbers 1
property showripindex 4
property timelimit 1
#
module new elock work:elock:NOFILE -nosplit
load symbol RTL_EQ0 work RTL(=) pin O output.right pinBus I0 input.left [11:0] pinBus I1 input.left [11:0] fillcolor 1
load symbol RTL_REG__BREG_8 workC GEN pin C input.neg.clk.left pin D input.left pin Q output.right fillcolor 1
load symbol RTL_INV work INV pin I0 input pin O output fillcolor 1
load symbol RTL_REG__BREG_8 work GEN pin C input.clk.left pin D input.left pin Q output.right fillcolor 1
load symbol input_register work:input_register:NOFILE HIERBOX pin Circ output.right pin Ld input.left pin Reset input.left pinBus Din input.left [2:0] pinBus Dout output.right [11:0] pinBus count output.right [1:0] boxcolor 1 fillcolor 2 minwidth 13%
load symbol Inputer work:Inputer:NOFILE HIERBOX pin D0 input.left pin D1 input.left pin D2 input.left pin D3 input.left pin D4 input.left pin D5 input.left pin D6 input.left pin D7 input.left pin Valid output.right pinBus Dout output.right [2:0] pinBus Input_light output.right [7:0] boxcolor 1 fillcolor 2 minwidth 13%
load symbol key_register work:key_register:NOFILE HIERBOX pin Ld input.left pin Reset input.left pin unlocked input.left pinBus Din input.left [2:0] pinBus Dout output.right [11:0] boxcolor 1 fillcolor 2 minwidth 13%
load symbol warning work:warning:NOFILE HIERBOX pin Clk input.left pin Reset input.left pin begin_count input.left pin unlocked input.left pin warn_sig output.right pinBus show_num output.right [0:8] boxcolor 1 fillcolor 2 minwidth 13%
load symbol RTL_AND work AND pin I0 input pin I1 input pin O output fillcolor 1
load symbol OBUF hdi_primitives[7:0] BUF pinBus O output [7:0] pinBus I input [7:0] fillcolor 1 sandwich 3 prop @bundle 8
load symbol IBUF hdi_primitives[7:0] BUF pinBus O output [7:0] pinBus I input [7:0] fillcolor 1 sandwich 3 prop @bundle 8
load port Check input -pg 1 -y 70
load port Clk input -pg 1 -y 370
load port warn_sig output -pg 1 -y 570
load port Red output -pg 1 -y 470
load port Circ output -pg 1 -y 190
load port Reset input -pg 1 -y 310
load port Green output -pg 1 -y 110
load portBus D input [7:0] -attr @name D[7:0] -pg 1 -y 150
load portBus countDown output [0:8] -attr @name countDown[0:8] -pg 1 -y 370
load portBus count output [1:0] -attr @name count[1:0] -pg 1 -y 230
load portBus input_light output [7:0] -attr @name input_light[7:0] -pg 1 -y 50
load inst Red_i RTL_INV work -attr @cell(#000000) RTL_INV -pg 1 -lvl 6 -y 470
load inst begin_count_reg RTL_REG__BREG_8 work -attr @cell(#000000) RTL_REG -pg 1 -lvl 5 -y 430
load inst inputer Inputer work:Inputer:NOFILE -autohide -attr @cell(#000000) Inputer -pinBusAttr Dout @name Dout[2:0] -pinBusAttr Input_light @name Input_light[7:0] -pg 1 -lvl 2 -y 120
load inst Correct_reg RTL_REG__BREG_8 workC -attr @cell(#000000) RTL_REG -pg 1 -lvl 5 -y 110
load inst begin_count0_i RTL_INV work -attr @cell(#000000) RTL_INV -pg 1 -lvl 4 -y 410
load inst Correct0_i RTL_EQ0 work -attr @cell(#000000) RTL_EQ -pinBusAttr I0 @name I0[11:0] -pinBusAttr I1 @name I1[11:0] -pg 1 -lvl 4 -y 120
load inst D[7:0]_IBUF_inst IBUF hdi_primitives[7:0] -attr @cell(#000000) IBUF -pg 1 -lvl 1 -y 150
load inst warn warning work:warning:NOFILE -autohide -attr @cell(#000000) warning -pinBusAttr show_num @name show_num[0:8] -pg 1 -lvl 6 -y 340
load inst warn_sig_i RTL_AND work -attr @cell(#000000) RTL_AND -pg 1 -lvl 6 -y 570
load inst warn_sig0_i RTL_AND work -attr @cell(#000000) RTL_AND -pg 1 -lvl 5 -y 550
load inst input_light[7:0]_OBUF_inst OBUF hdi_primitives[7:0] -attr @cell(#000000) OBUF -pg 1 -lvl 6 -y 50
load inst input_reg input_register work:input_register:NOFILE -autohide -attr @cell(#000000) input_register -pinBusAttr Din @name Din[2:0] -pinBusAttr Dout @name Dout[11:0] -pinBusAttr count @name count[1:0] -pg 1 -lvl 6 -y 180
load inst key_reg key_register work:key_register:NOFILE -autohide -attr @cell(#000000) key_register -pinBusAttr Din @name Din[2:0] -pinBusAttr Dout @name Dout[11:0] -pg 1 -lvl 3 -y 220
load net Val[2] -attr @rip Dout[2] -pin Correct0_i I0[2] -pin input_reg Dout[2]
load net countDown[8] -attr @rip show_num[8] -port countDown[8] -pin warn show_num[8]
load net input_light_OBUF[6] -attr @rip Input_light[6] -pin input_light[7:0]_OBUF_inst I[6] -pin inputer Input_light[6]
load net Val[9] -attr @rip Dout[9] -pin Correct0_i I0[9] -pin input_reg Dout[9]
load net D[4] -attr @rip D[4] -port D[4] -pin D[7:0]_IBUF_inst I[4]
load net input_light_OBUF[3] -attr @rip Input_light[3] -pin input_light[7:0]_OBUF_inst I[3] -pin inputer Input_light[3]
load net Key[2] -attr @rip Dout[2] -pin Correct0_i I1[2] -pin key_reg Dout[2]
load net Valid -pin begin_count_reg C -pin input_reg Ld -pin inputer Valid -pin key_reg Ld
netloc Valid 1 2 4 390 330 NJ 330 910 210 NJ
load net Key[9] -attr @rip Dout[9] -pin Correct0_i I1[9] -pin key_reg Dout[9]
load net input_light_OBUF[2] -attr @rip Input_light[2] -pin input_light[7:0]_OBUF_inst I[2] -pin inputer Input_light[2]
load net D_IBUF[1] -pin D[7:0]_IBUF_inst O[1] -pin inputer D1
load net Check -port Check -pin Correct_reg C
netloc Check 1 0 5 NJ 70 NJ 70 NJ 70 NJ 70 910J
load net D_num[2] -attr @rip Dout[2] -pin input_reg Din[2] -pin inputer Dout[2] -pin key_reg Din[2]
load net Red -port Red -pin Red_i O -pin warn_sig0_i I1
netloc Red 1 4 3 890 590 1090J 530 1430
load net countDown[7] -attr @rip show_num[7] -port countDown[7] -pin warn show_num[7]
load net timeout -pin warn warn_sig -pin warn_sig0_i I0
netloc timeout 1 4 3 890 510 NJ 510 1410
load net D_IBUF[6] -pin D[7:0]_IBUF_inst O[6] -pin inputer D6
load net warn_sig -port warn_sig -pin warn_sig_i O
netloc warn_sig 1 6 1 NJ
load net Val[0] -attr @rip Dout[0] -pin Correct0_i I0[0] -pin input_reg Dout[0]
load net input_light[0] -attr @rip 0 -port input_light[0] -pin input_light[7:0]_OBUF_inst O[0]
load net Clk -port Clk -pin warn Clk
netloc Clk 1 0 6 NJ 370 NJ 370 NJ 370 670J 350 NJ 350 NJ
load net begin_count0 -pin begin_count0_i O -pin begin_count_reg D -pin warn_sig_i I1
netloc begin_count0 1 4 2 890 490 1110
load net input_light[7] -attr @rip 7 -port input_light[7] -pin input_light[7:0]_OBUF_inst O[7]
load net D[7] -attr @rip D[7] -port D[7] -pin D[7:0]_IBUF_inst I[7]
load net Val[7] -attr @rip Dout[7] -pin Correct0_i I0[7] -pin input_reg Dout[7]
load net D_num[0] -attr @rip Dout[0] -pin input_reg Din[0] -pin inputer Dout[0] -pin key_reg Din[0]
load net Key[0] -attr @rip Dout[0] -pin Correct0_i I1[0] -pin key_reg Dout[0]
load net Key[7] -attr @rip Dout[7] -pin Correct0_i I1[7] -pin key_reg Dout[7]
load net Key[11] -attr @rip Dout[11] -pin Correct0_i I1[11] -pin key_reg Dout[11]
load net D[1] -attr @rip D[1] -port D[1] -pin D[7:0]_IBUF_inst I[1]
load net countDown[3] -attr @rip show_num[3] -port countDown[3] -pin warn show_num[3]
load net count[1] -attr @rip count[1] -port count[1] -pin input_reg count[1]
load net D[6] -attr @rip D[6] -port D[6] -pin D[7:0]_IBUF_inst I[6]
load net D_IBUF[4] -pin D[7:0]_IBUF_inst O[4] -pin inputer D4
load net D[0] -attr @rip D[0] -port D[0] -pin D[7:0]_IBUF_inst I[0]
load net input_light_OBUF[5] -attr @rip Input_light[5] -pin input_light[7:0]_OBUF_inst I[5] -pin inputer Input_light[5]
load net Val[8] -attr @rip Dout[8] -pin Correct0_i I0[8] -pin input_reg Dout[8]
load net countDown[4] -attr @rip show_num[4] -port countDown[4] -pin warn show_num[4]
load net input_light[5] -attr @rip 5 -port input_light[5] -pin input_light[7:0]_OBUF_inst O[5]
load net Reset -port Reset -pin begin_count0_i I0 -pin input_reg Reset -pin key_reg Reset -pin warn Reset
netloc Reset 1 0 6 NJ 310 NJ 310 410 350 650 370 NJ 370 1090
load net Val[5] -attr @rip Dout[5] -pin Correct0_i I0[5] -pin input_reg Dout[5]
load net Key[8] -attr @rip Dout[8] -pin Correct0_i I1[8] -pin key_reg Dout[8]
load net warn_sig0 -pin warn_sig0_i O -pin warn_sig_i I0
netloc warn_sig0 1 5 1 1070
load net D_IBUF[2] -pin D[7:0]_IBUF_inst O[2] -pin inputer D2
load net Key[5] -attr @rip Dout[5] -pin Correct0_i I1[5] -pin key_reg Dout[5]
load net Circ -port Circ -pin input_reg Circ
netloc Circ 1 6 1 NJ
load net D_IBUF[7] -pin D[7:0]_IBUF_inst O[7] -pin inputer D7
load net countDown[1] -attr @rip show_num[1] -port countDown[1] -pin warn show_num[1]
load net input_light[6] -attr @rip 6 -port input_light[6] -pin input_light[7:0]_OBUF_inst O[6]
load net Val[1] -attr @rip Dout[1] -pin Correct0_i I0[1] -pin input_reg Dout[1]
load net Val[6] -attr @rip Dout[6] -pin Correct0_i I0[6] -pin input_reg Dout[6]
load net D[3] -attr @rip D[3] -port D[3] -pin D[7:0]_IBUF_inst I[3]
load net Green -pin Correct_reg Q -port Green -pin Red_i I0 -pin key_reg unlocked -pin warn unlocked
netloc Green 1 2 5 430 150 690J 170 NJ 170 1110 110 NJ
load net input_light[3] -attr @rip 3 -port input_light[3] -pin input_light[7:0]_OBUF_inst O[3]
load net Val[3] -attr @rip Dout[3] -pin Correct0_i I0[3] -pin input_reg Dout[3]
load net Key[6] -attr @rip Dout[6] -pin Correct0_i I1[6] -pin key_reg Dout[6]
load net D_num[1] -attr @rip Dout[1] -pin input_reg Din[1] -pin inputer Dout[1] -pin key_reg Din[1]
load net Key[10] -attr @rip Dout[10] -pin Correct0_i I1[10] -pin key_reg Dout[10]
load net input_light_OBUF[7] -attr @rip Input_light[7] -pin input_light[7:0]_OBUF_inst I[7] -pin inputer Input_light[7]
load net countDown[2] -attr @rip show_num[2] -port countDown[2] -pin warn show_num[2]
load net count[0] -attr @rip count[0] -port count[0] -pin input_reg count[0]
load net begin_count -pin begin_count_reg Q -pin warn begin_count
netloc begin_count 1 5 1 1090
load net input_light_OBUF[1] -attr @rip Input_light[1] -pin input_light[7:0]_OBUF_inst I[1] -pin inputer Input_light[1]
load net input_light_OBUF[4] -attr @rip Input_light[4] -pin input_light[7:0]_OBUF_inst I[4] -pin inputer Input_light[4]
load net countDown[6] -attr @rip show_num[6] -port countDown[6] -pin warn show_num[6]
load net D[2] -attr @rip D[2] -port D[2] -pin D[7:0]_IBUF_inst I[2]
load net Correct0 -pin Correct0_i O -pin Correct_reg D
netloc Correct0 1 4 1 N
load net D_IBUF[0] -pin D[7:0]_IBUF_inst O[0] -pin inputer D0
load net Key[3] -attr @rip Dout[3] -pin Correct0_i I1[3] -pin key_reg Dout[3]
load net Val[11] -attr @rip Dout[11] -pin Correct0_i I0[11] -pin input_reg Dout[11]
load net D_IBUF[5] -pin D[7:0]_IBUF_inst O[5] -pin inputer D5
load net input_light[4] -attr @rip 4 -port input_light[4] -pin input_light[7:0]_OBUF_inst O[4]
load net Val[4] -attr @rip Dout[4] -pin Correct0_i I0[4] -pin input_reg Dout[4]
load net input_light_OBUF[0] -attr @rip Input_light[0] -pin input_light[7:0]_OBUF_inst I[0] -pin inputer Input_light[0]
load net Val[10] -attr @rip Dout[10] -pin Correct0_i I0[10] -pin input_reg Dout[10]
load net countDown[5] -attr @rip show_num[5] -port countDown[5] -pin warn show_num[5]
load net input_light[1] -attr @rip 1 -port input_light[1] -pin input_light[7:0]_OBUF_inst O[1]
load net Key[4] -attr @rip Dout[4] -pin Correct0_i I1[4] -pin key_reg Dout[4]
load net countDown[0] -attr @rip show_num[0] -port countDown[0] -pin warn show_num[0]
load net D_IBUF[3] -pin D[7:0]_IBUF_inst O[3] -pin inputer D3
load net Key[1] -attr @rip Dout[1] -pin Correct0_i I1[1] -pin key_reg Dout[1]
load net D[5] -attr @rip D[5] -port D[5] -pin D[7:0]_IBUF_inst I[5]
load net input_light[2] -attr @rip 2 -port input_light[2] -pin input_light[7:0]_OBUF_inst O[2]
load netBundle @Key 12 Key[11] Key[10] Key[9] Key[8] Key[7] Key[6] Key[5] Key[4] Key[3] Key[2] Key[1] Key[0] -autobundled
netbloc @Key 1 3 1 650
load netBundle @input_light 8 input_light[7] input_light[6] input_light[5] input_light[4] input_light[3] input_light[2] input_light[1] input_light[0] -autobundled
netbloc @input_light 1 6 1 NJ
load netBundle @count 2 count[1] count[0] -autobundled
netbloc @count 1 6 1 NJ
load netBundle @D_IBUF 8 D_IBUF[7] D_IBUF[6] D_IBUF[5] D_IBUF[4] D_IBUF[3] D_IBUF[2] D_IBUF[1] D_IBUF[0] -autobundled
netbloc @D_IBUF 1 1 1 170
load netBundle @Val 12 Val[11] Val[10] Val[9] Val[8] Val[7] Val[6] Val[5] Val[4] Val[3] Val[2] Val[1] Val[0] -autobundled
netbloc @Val 1 3 4 650 10 NJ 10 NJ 10 1410
load netBundle @input_light_OBUF 8 input_light_OBUF[7] input_light_OBUF[6] input_light_OBUF[5] input_light_OBUF[4] input_light_OBUF[3] input_light_OBUF[2] input_light_OBUF[1] input_light_OBUF[0] -autobundled
netbloc @input_light_OBUF 1 2 4 390J 50 NJ 50 NJ 50 NJ
load netBundle @countDown 9 countDown[0] countDown[1] countDown[2] countDown[3] countDown[4] countDown[5] countDown[6] countDown[7] countDown[8] -autobundled
netbloc @countDown 1 6 1 NJ
load netBundle @D_num 3 D_num[2] D_num[1] D_num[0] -autobundled
netbloc @D_num 1 2 4 410 170 670J 190 NJ 190 N
load netBundle @D 8 D[7] D[6] D[5] D[4] D[3] D[2] D[1] D[0] -autobundled
netbloc @D 1 0 1 NJ
levelinfo -pg 1 0 30 210 500 760 960 1200 1450 -top 0 -bot 610
show
fullfit
#
# initialize ictrl to current module elock work:elock:NOFILE
ictrl init topinfo |
ictrl layer glayer install
ictrl layer glayer config ibundle 1
ictrl layer glayer config nbundle 0
ictrl layer glayer config pbundle 0
ictrl layer glayer config cache 1
