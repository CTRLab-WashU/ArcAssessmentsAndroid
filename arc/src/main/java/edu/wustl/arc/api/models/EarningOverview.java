/*
  Copyright (c) 2022 Washington University in St. Louis

  Washington University in St. Louis hereby grants to you a non-transferable,
  non-exclusive, royalty-free license to use and copy the computer code
  provided here (the "Software").  You agree to include this license and the
  above copyright notice in all copies of the Software.  The Software may not
  be distributed, shared, or transferred to any third party.  This license does
  not grant any rights or licenses to any other patents, copyrights, or other
  forms of intellectual property owned or controlled by
  Washington University in St. Louis.

  YOU AGREE THAT THE SOFTWARE PROVIDED HEREUNDER IS EXPERIMENTAL AND IS PROVIDED
  "AS IS", WITHOUT ANY WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING
  WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR
  PURPOSE, OR NON-INFRINGEMENT OF ANY THIRD-PARTY PATENT, COPYRIGHT, OR ANY OTHER
  THIRD-PARTY RIGHT.  IN NO EVENT SHALL THE CREATORS OF THE SOFTWARE OR WASHINGTON
  UNIVERSITY IN ST LOUIS BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL, OR
  CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN ANY WAY CONNECTED WITH THE SOFTWARE,
  THE USE OF THE SOFTWARE, OR THIS AGREEMENT, WHETHER IN BREACH OF CONTRACT, TORT
  OR OTHERWISE, EVEN IF SUCH PARTY IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
*/
package edu.wustl.arc.api.models;

import androidx.annotation.Nullable;
import edu.wustl.arc.study.Earnings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EarningOverview {

    public String total_earnings;
    public Integer cycle;
    public String cycle_earnings;
    public List<Goal> goals;
    public List<Achievement> new_achievements;

    public static EarningOverview getTestObject() {
        EarningOverview overview = new EarningOverview();
        overview.cycle = 0;
        overview.cycle_earnings = "$0.50";
        overview.total_earnings = "$13.50";

        Achievement testAchievement = new Achievement();
        testAchievement.name = "test-session";
        testAchievement.amount_earned = "$0.50";
        overview.new_achievements.add(testAchievement);

//        Achievement twentyOneAchievement = new Achievement();
//        twentyOneAchievement.name = "21-sessions";
//        twentyOneAchievement.amount_earned = "$5.00";
//        overview.new_achievements.add(twentyOneAchievement);

        Goal fourOutOfFour = new Goal();
        fourOutOfFour.name = "4-out-of-4";
        fourOutOfFour.completed = false;
        fourOutOfFour.amount_earned = "$0.00";
        fourOutOfFour.progress = 1;
        fourOutOfFour.value = "$1.00";
        fourOutOfFour.progress_components = new ArrayList<Integer>() {{
            add(33);
            add(100);
            add(0);
            add(0);
        }};
        overview.goals.add(fourOutOfFour);

        Goal twoADay = new Goal();
        twoADay.name = "2-a-day";
        twoADay.completed = false;
        twoADay.amount_earned = "$0.00";
        twoADay.progress = 1;
        twoADay.value = "$6.00";
        twoADay.progress_components = new ArrayList<Integer>() {{
            add(100);
            add(100);
            add(100);
            add(100);
            add(50);
            add(0);
            add(0);
        }};
        overview.goals.add(twoADay);

        Goal twentyOneSessions = new Goal();
        twentyOneSessions.name = "21-sessions";
        twentyOneSessions.completed = true;
        twentyOneSessions.completed_on = 1566251711L;
        twentyOneSessions.amount_earned = "$5.00";
        twentyOneSessions.progress = 13;
        twentyOneSessions.value = "$5.00";
        overview.goals.add(twentyOneSessions);

        Collections.sort(overview.goals,new GoalComparator());

        return overview;
    }

    public EarningOverview() {
        total_earnings = new String();
        cycle = new Integer(0);
        cycle_earnings = new String();
        goals = new ArrayList<>();
        new_achievements = new ArrayList<>();
    }

    public boolean hasAchievementFor(Goal goal) {
        for(EarningOverview.Achievement achievement : new_achievements) {
            if(achievement.name.equals(goal.name)) {
                return true;
            }
        }
        return false;
    }


    public static class Goal {

        public String name;
        public String value;
        public Integer progress;
        public String amount_earned;
        public Boolean completed;
        public Long completed_on;
        public List<Integer> progress_components;

        public Goal() {
            name = new String();
            value = new String();
            progress = new Integer(0);
            amount_earned = new String();
            completed = new Boolean(false);
        }
    }

    public static class Achievement {

        public String name;
        public String amount_earned;

        public Achievement() {
            name = new String();
            amount_earned = new String();
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return name.equals(obj);
        }
    }

    public static class GoalComparator implements Comparator<Goal> {
        @Override
        public int compare(Goal a, Goal b) {
            return getIndex(a.name).compareTo(getIndex(b.name));
        }

        Integer getIndex(String name) {
            if(name.equals(Earnings.FOUR_OUT_OF_FOUR)) {
                return 0;
            } else if(name.equals(Earnings.TWO_A_DAY)) {
                return 1;
            } else if(name.equals(Earnings.TWENTY_ONE_SESSIONS)) {
                return 2;
            }
            return -1;
        }

    }

    public static EarningOverview createEarningOverview(
                int cycle, String cycle_earnings, String total_earnings,

                boolean complete4, String earned4, int progress4,String value4, ArrayList<Integer> components4,

                boolean complete2, String earned2, int progress2, String value2,  ArrayList<Integer> components2,

                boolean complete21, String earned21, int progress21, String value21
                )
    {
        EarningOverview overview = new EarningOverview();
        overview.cycle = cycle;
        overview.cycle_earnings = cycle_earnings;
        overview.total_earnings = total_earnings;


        Goal fourOutOfFour = new Goal();
        fourOutOfFour.name = "4-out-of-4";
        fourOutOfFour.completed = complete4;
        fourOutOfFour.amount_earned = earned4;
        fourOutOfFour.progress = progress4;
        fourOutOfFour.value = value4;
        fourOutOfFour.progress_components =  components4;
        overview.goals.add(fourOutOfFour);

        Goal twoADay = new Goal();
        twoADay.name = "2-a-day";
        twoADay.completed = complete2;
        twoADay.amount_earned = earned2;
        twoADay.progress = progress2;
        twoADay.value = value2;
        twoADay.progress_components = components2;
        overview.goals.add(twoADay);

        Goal twentyOneSessions = new Goal();
        twentyOneSessions.name = "21-sessions";
        twentyOneSessions.completed = complete21;
        twentyOneSessions.amount_earned = earned21;
        twentyOneSessions.progress = progress21;
        twentyOneSessions.value = value21;
        overview.goals.add(twentyOneSessions);

        Collections.sort(overview.goals,new GoalComparator());

        return overview;

    }

}
