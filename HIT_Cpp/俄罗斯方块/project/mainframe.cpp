#include "mainframe.h"
#include "ui_mainframe.h"
#include <QGridLayout>

MainFrame::MainFrame(QWidget *parent) :
    QGraphicsView(parent), stopped(false),
    config(new GameConfig()), started(false)
{
    gameBoard = new GameBoard(config, this);
    welcomBoard = new WelcomBoard(config, this);
    stopMenu = new StopMenu(this);
    QGridLayout *mainLayout = new QGridLayout(this);
    mainLayout->addWidget(welcomBoard, 0, 0, Qt::AlignCenter);
    mainLayout->addWidget(gameBoard, 0, 0, Qt::AlignCenter);
    stopMenu->setFixedHeight(300);
    mainLayout->addWidget(stopMenu, 0, 0, Qt::AlignCenter);

    connect(gameBoard, SIGNAL(menuSig()), this, SLOT(showStopMenu()));
    connect(stopMenu, SIGNAL(backToGame()), this, SLOT(backToGame()));
    connect(stopMenu, SIGNAL(restartGame()), this, SLOT(restartGame()));
    connect(stopMenu, SIGNAL(returnToMain()), this, SLOT(returnToMain()));

    switchBoard();
}

MainFrame::~MainFrame()
{

}

void MainFrame::switchBoard()
{
    if(started)
    {
        welcomBoard->hide();
        gameBoard->show();
        gameBoard->setFocus();
        if(stopped)
            stopMenu->show();
        else
            stopMenu->hide();
    }
    else
    {
        welcomBoard->show();
        gameBoard->hide();
        stopMenu->hide();
    }
}

//slots receive signal from welcomeBoard
void MainFrame::backToGame()
{
    started = true;
    stopped = false;
    switchBoard();
    gameBoard->resumeGame();
}

void MainFrame::returnToMain()
{
    started = false;
    stopped = false;
    switchBoard();
    gameBoard->quitGame();
}

void MainFrame::restartGame()
{
    gameBoard->quitGame();
    started = true;
    stopped = false;
    switchBoard();
    gameStart();
}

void MainFrame::gameStart()
{
    started = true;
    stopped = false;
    switchBoard();
    gameBoard->initGame();
}

void MainFrame::showConfigMenu()
{

}

void MainFrame::showHelpMenu()
{

}

void MainFrame::showStopMenu()
{
    stopped = true;
    switchBoard();
}

void MainFrame::gameExit()
{
    QApplication::exit();
}
