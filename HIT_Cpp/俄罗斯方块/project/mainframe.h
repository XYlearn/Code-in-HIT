#ifndef MAINFRAME_H
#define MAINFRAME_H

#include <QDialog>
#include <QPushButton>
#include <QLCDNumber>
#include "gameboard.h"
#include "gameconfig.h"
#include "welcomboard.h"
#include "stopmenu.h"

#include<QGraphicsView>

namespace Ui {
class MainFrame;
}

class MainFrame : public QGraphicsView
{
    Q_OBJECT

public:
    constexpr static int PanelHeight = 900;
    constexpr static int PanelWidth = 800;
    explicit MainFrame(QWidget *parent = 0);
    ~MainFrame();
    void switchBoard();

private:
    Ui::MainFrame *ui;
    bool started;
    bool stopped;

    GameBoard *gameBoard;
    WelcomBoard *welcomBoard;
    StopMenu *stopMenu;
    GameConfig *config;

public slots:
    void returnToMain();
    void backToGame();
    void restartGame();

    void gameStart();
    void showStopMenu();
    void showConfigMenu();
    void showHelpMenu();
    void gameExit();

};

#endif // MAINFRAME_H
