#ifndef WELCOMBOARD_H
#define WELCOMBOARD_H

#include <QWidget>
#include <QPushButton>
#include <gameconfig.h>

class WelcomBoard : public QWidget
{
    Q_OBJECT
public:
    explicit WelcomBoard(GameConfig *config, QWidget *parent = nullptr);

private:
    QPushButton *startButton;
    QPushButton *configButton;
    QPushButton *helpButton;
    QPushButton *quitButton;
    GameConfig *config;

signals:
    void startButtonPush();
    void configButtonPush();
    void helpButtonPush();
    void quitButtonPush();

public slots:
};

class HelpBoard : public QWidget
{
    Q_OBJECT
public:
    HelpBoard(QWidget *parent = nullptr);
    ~HelpBoard() {}
private:
};


#endif // WELCOMBOARD_H
