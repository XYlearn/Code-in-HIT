#include "mainframe.h"
#include <QApplication>
#include <QSizePolicy>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    MainFrame w;
    w.setFixedHeight(w.PanelHeight);
    w.setFixedWidth(w.PanelWidth);
    w.show();

    return a.exec();
}
