#include <QObject>
#include "gameboard.h"
#include "mainframe.h"
#include <QGraphicsItem>
#include <QVBoxLayout>
#include <QHBoxLayout>

GameBoard::GameBoard(GameConfig *config, QWidget *parent)
    : QGraphicsView(parent), config(config), levelUpFlag(false),
      menuButton(new QPushButton(tr("&Menu"), this)),
      levelLCD(new QLCDNumber(this)),scoreLCD(new QLCDNumber(this)),
      item(new TerisItem), nextItem(new TerisItem)
{
    this->setFixedHeight(MainFrame::PanelHeight);
    this->setFixedWidth(MainFrame::PanelWidth);
    this->setHorizontalScrollBarPolicy(Qt::ScrollBarAlwaysOff);
    this->setVerticalScrollBarPolicy(Qt::ScrollBarAlwaysOff);
    QVBoxLayout *mainLayout = new QVBoxLayout(this);
    QHBoxLayout *topLayout = new QHBoxLayout(this);
    QHBoxLayout *midLayout = new QHBoxLayout(this);
    QHBoxLayout *bottomLayout = new QHBoxLayout(this);

    topLayout->setAlignment(Qt::AlignLeft);
    menuButton->setFixedHeight(80);
    menuButton->setFixedWidth(80);
    topLayout->addWidget(menuButton);
    topLayout->addSpacing(150);
    statusLabel = new QLabel(this);
    statusLabel->setText("");
    statusLabel->setWordWrap(true);
    statusLabel->setFont(QFont("", 30, QFont::Bold));
    statusLabel->setStyleSheet("color:red");
    topLayout->addWidget(statusLabel);
    topLayout->addStretch();

    levelLCD->setFixedHeight(60);
    levelLCD->setFixedWidth(60);
    levelLCD->setDigitCount(1);
    levelLabel = new QLabel(this);
    levelLabel->setText(tr("level"));
    midLayout->setAlignment(Qt::AlignCenter | Qt::AlignLeft);
    midLayout->addWidget(levelLabel);
    midLayout->addWidget(levelLCD);

    bottomLayout->setAlignment(Qt::AlignCenter);
    scoreLCD->setFixedHeight(60);
    scoreLCD->setFixedWidth(500);
    scoreLCD->setDigitCount(10);
    scoreLabel = new QLabel(this);
    scoreLabel->setText(tr("score"));
    bottomLayout->addWidget(scoreLabel);
    bottomLayout->addWidget(scoreLCD);

    mainLayout->addLayout(topLayout);
    mainLayout->addLayout(midLayout);
    mainLayout->addLayout(bottomLayout);
    mainLayout->setStretchFactor(topLayout, 1);
    mainLayout->setStretchFactor(midLayout, 10);
    mainLayout->setStretchFactor(bottomLayout, 2);

    this->setLayout(mainLayout);

    connect(item, SIGNAL(gameOver()), this, SLOT(gameOver()));
    connect(menuButton, SIGNAL(clicked(bool)), this, SLOT(stopGame()));
}

void GameBoard::initGame()
{
    QGraphicsScene *scene = new QGraphicsScene;
    scene->setSceneRect(5, 5, 800, 900);
    this->setScene(scene);
    //450 * 630 = 15 * 21
    topBorder = scene->addLine(150-1, 150-1, 600+1, 150-1);
    leftBorder = scene->addLine(150-1, 150-1, 150-1, 780+1);
    bottomBorder = scene->addLine(150-1, 780+1, 600+1, 780+1);
    rightBorder = scene->addLine(600+1, 150-1, 600+1, 780+1);

    item->newItem(QPoint(360, 150 + 30*2));
    nextItem->newItem(QPoint(600 + 3*30, 150 + 30));

    scene->addItem(item);
    scene->addItem(nextItem);
    item->setFocus();

    timerID = startTimer(config->getInterval());
}

void GameBoard::quitGame()
{
    scoreLCD->display(0);
    levelLCD->display(0);
    killTimer(timerID);
    scene()->clear();
    item = new TerisItem;
    nextItem = new TerisItem;
}

GameBoard::~GameBoard()
{
}

void GameBoard::timerEvent(QTimerEvent *event)
{
    if(item->moveDown())
        return;
    else
    {
        statusLabel->setText("");
        levelUpFlag = false;
        item->clearItem(false);
        clearRow();

        item->newItem(QPoint(360, 150 + 30*2), nextItem->getShape());

        nextItem->clearItem(true);
        nextItem->newItem(QPoint(600 + 3*30, 150 + 30));
    }
}

void GameBoard::clearRow()
{
    /*(150,150) -> (600, 780)
       15 * 21*/
    int row = 0;
    QSet<int> rowsDeleted;
    for(int y = 750; y >= 150; y-=30)
    {
        QList<QGraphicsItem *> rowItems = scene()->items(QRectF(150-1, y-1, 450+2, 30+2), Qt::ContainsItemShape);
        if(rowItems.count() == 15)
        {
            for(QGraphicsItem *item : rowItems)
            {
                TerisSingleItem * singleItem = (TerisSingleItem *)item;
                singleItem->deleteLater();
            }

            rowsDeleted << row;
        }
        else if(rowsDeleted.size())
            break;
        row++;
    }
    int offset = 0;
    for(int row : rowsDeleted)
    {
        QList<QGraphicsItem *>lst = scene()->items(QRectF(150-1, 150-1 , 450+2, 630-30*(row+1-offset)+2), Qt::ContainsItemShape);
        for(QGraphicsItem * item : lst)
        {
            TerisSingleItem *singleItem = (TerisSingleItem *)item;
            if(singleItem->y() < 750)
                singleItem->moveBy(0, 30);
        }
        ++offset;
    }
    updateScore(rowsDeleted.size());

}

void GameBoard::gameOver()
{
    killTimer(timerID);
    statusLabel->setText(tr("Game Over"));
    statusLabel->show();
}


void GameBoard::stopGame()
{
    killTimer(timerID);
    emit menuSig();
}

void GameBoard::resumeGame()
{
    timerID = startTimer(config->getInterval());
}

void GameBoard::updateScore(int rows)
{
    int increase = (config->getLevel()+1) * rows * (rows/2.0+1) * 10;
    int score = scoreLCD->intValue() + increase;
    while(scoreLCD->checkOverflow(score))
    {
        scoreLCD->setDigitCount(scoreLCD->digitCount() + 1);
    }
    scoreLCD->display(QString("%1").arg(score));
    levelUp();
}

void GameBoard::levelUp()
{
    if(config->getLevel() >= config->maxLevel
            || scoreLCD->intValue() < config->nextLevelScore())
        return;
    else
    {
        config->levelUp();
        levelLCD->display(QString("%1").arg(config->getLevel()));
        killTimer(timerID);
        timerID = startTimer(config->getInterval());
        levelUpFlag = true;
        statusLabel->setText("Level Up");
    }

}
