#ifndef GAMECONFIG_H
#define GAMECONFIG_H
#include <vector>

class GameConfig
{
public:
    const static int maxLevel = 5;

    GameConfig();
    int getInterval()
    {
        if(level <= maxLevel) return intervals[level];
        else return 300;
    }
    int getLevel() {return this->level;}
    int nextLevelScore() {return nextLevel[level];}
    void levelUp() {++level;}
    void setLevel(int level) {if(level < maxLevel) this->level = level;}

private:
    const int nextLevel[maxLevel] = {300, 500, 1500, 4000, 10000};
    const int intervals[maxLevel+1] = {300, 250 ,200 ,150, 100, 50};
    int level;
    bool sound;
};

#endif // GAMECONFIG_H
