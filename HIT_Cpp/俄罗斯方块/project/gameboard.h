#ifndef GAMEBOARD_H
#define GAMEBOARD_H

#include <QGraphicsView>
#include <QTime>
#include <QPushButton>
#include <QGraphicsItem>
#include <QLCDNumber>
#include <QLabel>
#include "gameconfig.h"
#include "terisitem.h"
#include "stopmenu.h"

class GameBoard : public QGraphicsView
{
    Q_OBJECT
public:
    explicit GameBoard(GameConfig *config, QWidget *parent = nullptr);
    ~GameBoard();

    void initGame();
    void quitGame();

protected:
    void timerEvent(QTimerEvent *event);

private:
    int timerID;

    QPushButton *menuButton;
    QLabel *levelLabel;
    QLabel *scoreLabel;
    QLabel *statusLabel;
    QLCDNumber *levelLCD;
    QLCDNumber *scoreLCD;

    QGraphicsLineItem *topBorder;
    QGraphicsLineItem *leftBorder;
    QGraphicsLineItem *rightBorder;
    QGraphicsLineItem *bottomBorder;

    TerisItem *item;
    TerisItem *nextItem;

    QList<TerisSingleItem *> dropItems;

    GameConfig *config;

    bool levelUpFlag;
private:
    void clearRow();
    void levelUp();

signals:
    void menuSig();

public slots:
    void updateScore(int rows);
    void gameOver();
    void stopGame();
    void resumeGame();

};

#endif // GAMEBOARD_H
