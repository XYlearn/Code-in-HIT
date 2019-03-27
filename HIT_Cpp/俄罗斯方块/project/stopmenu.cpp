#include "stopmenu.h"
#include <QHBoxLayout>
#include <QGraphicsView>
#include <QPushButton>
#include <QWidget>

StopMenu::StopMenu(QWidget *parent) : QWidget(parent)
{
    returnButton = new QPushButton(tr("&Return"), this);
    restartButton = new QPushButton(tr("&Restart"), this);
    backButton = new QPushButton(tr("&Back"), this);
    QHBoxLayout *layout = new QHBoxLayout(this);

    returnButton->setFixedHeight(100);
    returnButton->setFixedWidth(100);
    restartButton->setFixedHeight(100);
    restartButton->setFixedWidth(100);
    backButton->setFixedHeight(100);
    backButton->setFixedWidth(100);

    layout->addWidget(returnButton, Qt::AlignCenter);
    layout->addSpacing(50);
    layout->addWidget(backButton, Qt::AlignCenter);
    layout->addSpacing(50);
    layout->addWidget(restartButton, Qt::AlignCenter);

    connect(returnButton, SIGNAL(clicked(bool)), this, SIGNAL(returnToMain()));
    connect(restartButton, SIGNAL(clicked(bool)), this, SIGNAL(restartGame()));
    connect(backButton, SIGNAL(clicked(bool)), this, SIGNAL(backToGame()));

    this->setLayout(layout);
}
