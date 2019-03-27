#include "welcomboard.h"
#include <QVBoxLayout>

WelcomBoard::WelcomBoard(GameConfig *config, QWidget *parent)
    : QWidget(parent), config(config),
      startButton(new QPushButton(tr("&Start"), this)),
      configButton(new QPushButton(tr("&Config"), this)),
      helpButton(new QPushButton((tr("&Help")), this)),
      quitButton(new QPushButton((tr("&Quit")), this))
{
    startButton->setFixedWidth(100);
    startButton->setFixedHeight(60);
    configButton->setFixedWidth(100);
    configButton->setFixedHeight(60);
    helpButton->setFixedWidth(100);
    helpButton->setFixedHeight(60);
    quitButton->setFixedWidth(100);
    quitButton->setFixedHeight(60);

    QVBoxLayout *layout = new QVBoxLayout(this);
    layout->setAlignment(Qt::AlignCenter);
    layout->addWidget(startButton);
    layout->addSpacing(50);
    configButton->hide();
    helpButton->hide();
    //layout->addWidget(configButton);
    //layout->addSpacing(50);
    //layout->addWidget(helpButton);
    //layout->addSpacing(50);
    layout->addWidget(quitButton);
    setLayout(layout);

    connect(startButton, SIGNAL(clicked(bool)), parent, SLOT(gameStart()));
    connect(configButton, SIGNAL(clicked(bool)), parent, SLOT(showConfigMenu()));
    connect(helpButton, SIGNAL(clicked(bool)), parent, SLOT(showHelpMenu()));
    connect(quitButton, SIGNAL(clicked(bool)), parent, SLOT(gameExit()));
}
