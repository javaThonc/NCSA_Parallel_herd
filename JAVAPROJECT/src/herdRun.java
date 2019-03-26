import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.FileNotFoundException;
        import java.io.PrintWriter;
        import java.io.UnsupportedEncodingException;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.Random;
//import
        import static java.lang.Math.ceil;

public class herdRun {
    public static void main(String[] args) throws InterruptedException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("database.txt","UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int LimitCalves = 83,TbInfectionYN,TBTestControl,TimeIntervalTests,NumTbInfHeif,NumConsNegWHTRemoval,MaxNumAssuranceTest;
        for (int scenarioSim = 1; scenarioSim<=4; scenarioSim++){
            if (scenarioSim== 1){
                // Scenario 1
                // 1 bTB. Alt, B. 8/1, 334 iterations only.
                herdParameter.herdLimit= 1000; //
                herdParameter.allsusceptible = 1;
                herdParameter.ControlCullTest =0;
                herdParameter.ParityTest = 3;
                herdParameter.TimetoCull = 90;
                herdParameter.ControlCullLowMilk = 0;
                herdParameter.LowMilkDays = 90;
                herdParameter.LowMilkCullNow = 1;
                herdParameter.ControlCullCalvesTest =1;
            }
            else if (scenarioSim== 2) {
                // 5bTB, Alt, B
                herdParameter.herdLimit= 1000; //
                // Tb Parameters
                LimitCalves= 67; // for 1000 cows: 67 calves, 500: 34 calves, 100: 7 calves
            }
            else if (scenarioSim== 3 ){
                // 10bTB, Alt, B
                herdParameter.herdLimit= 1000; //
                // Tb Parameters
                LimitCalves= 67; // for 1000 cows: 67 calves, 500: 34 calves, 100: 7 calves
            }

            else if (scenarioSim== 4 ){
                // Base1pc_AC1HC // Horizontal A-C Transmission Rate corrected (v1)
                herdParameter.herdLimit= 1000; //
                // Tb Parameters
                LimitCalves= 67; // for 1000 cows: 67 calves, 500: 34 calves, 100: 7 calves
            }
            long startTime = System.currentTimeMillis();
            for(int jj = 1; jj <= herdParameter.MaxSim; jj++) {
                startTime = System.currentTimeMillis();
                double[][] herd = herdHelperFunc.DataImport();
                for (int i = 0; i < herdHelperFunc.getSize(); i++){
                    herd[i][0]=0;
                    herd[i][19]=0;
                    if(herd[i][3]==6){
                        herd[i][3]=5;
                    }
                    else if(herd[i][3]==8){
                        herd[i][3]=7;
                    }else{
                        herd[i][3]=1;
                    }
                }
                double id = herd[herdHelperFunc.getSize() - jj][1];//max ID

                String[] HerdName = {"Time", "ID", "Age", "InfStatus", "LactStatus", "Parity", "ParityAge",
                        "PregnantDays", "FailedAI", "DIM", "MAPDays", "MilkProd", "FeedCosts", "MilkRev", "Dead", "MilkCostDisc", "MilkRevDisc", "Inseminations", "TimetoCullFailAI"};

                String[] TotalName = {"Time", "Total_Cows", "Suscept", "Latent", "LowShedders",
                        "HighShedders", "CalvesS", "CalvesI", "HeifersS", "HeifersI", "CowInfHori_S",
                        "CowInfHori_Latent", "CowInfHori_Y1", "MaleCalves", "NumDeadCows", "NumDeadCalves",
                        "NumDeadHeifers", "OpenCows"}; // store number of animals, and transmissions

                String[] DeathsText = {"S", "L", "Y1", "Y2", "SC", "IC", "SH", "IH"};
                //
                // // Results from Whole Herd Test (TB)
                String[] WHTTriggerText = {"time", "Tested(1/0)", "PositiveTbResults(1/0)", "NumPosTestTbAll",
                        "NumTruePosTbAll", "NumTrueInfectedTbAll", "Occult(H+Cow)",
                        "Reactive(H+C)", "Infectious(H+C)", "Occult(Calves)", "Reactive+Infectious(Calves)"};
                // COSTS AND INTERVENTIONS

                // The initial cost of calf (150)
                int CostCalf = 150; // Cost of a newborn calf alive (Male or Female), NOT MARKET VALUE. (Karszes,pg 3)

                int MaleCalfPrice = 150; // Price of a newborn male calf
                int FemaleCalfPrice = 250; // Price of a female calf (they are culled once their number pass a limit)
                // (100 margin)
                int CulledCowPrice = 660; // Price of a culled cow due unable to get pregnant or over parity. av cull weight: 1600 Lb, at $100/cwt (50// dressing)
                int MatureWeight = 600; // Mature Weight of a Pregnant Cow in Kg.
                double CullCowPriceperKg = 1.1; // estimated from the AMS.USDA Reports
                double WeightLossY2Perc = 0.10; // weight loss of Y2 compared to non Y2. (10//)
                double HeiferReplCost = 2200 * 1.1; // Costs estimated from Model. (// margin)

                int managementChanges = 0; // 1/0 if incorporting management changes or interventions.
                int NewManagementCostperHead = 0; //55/365; // Cost of changing management practices per animal per day
                int CostInterventionCows = 0;    // Cost of Intervention strategy per Cow PER DAY
                int CostInterventionHeifers = 0; // Cost of Intervention strategy per Heifer PER DAY
                int CostInterventionCalves = 0;  // Cost of Intervention strategy per Calf PER DAY

                double HighShedFeedCostFactorMilkProd = 1.00; // Multiplicative factor in feed cost for high shedders under milk production
                double HighShedFeedCostFactorNoMilk = 1.00;  // Multiplicative factor in feed cost for high shedders under NO milk production

                int InsemCost = 20; // USD. Insemination Cost per service (15 Semen + 5 Vet)
                int PregnancyDiagnosis = 8; // USD. Cost of Pregnancy diagnosis,42-60-220 days after Insemination

                double FixedVarsCostDay = 2.5; // This is non-feed variable costs and fixed costs per cow per day > 720 days
                int maxMAPDays = 300; // Maximum number of MAP DAYS for the Milk Produciton Function

                double irate = 0.05; // Set Yearly discount rate
                double dailyirate = 1 - Math.exp(-irate / 365); // Daily discount rate

                // Testing variables, Current Protocol USDA
                int TestingPhase = 0; // Initialize
                int lastRemovalday = 0; // initialization of time of last removal test
                int timeHeifersIndemnityEnter = 0; // Initialization time to include indemnity heifers

                //////////////////////////////////// TOTAL HERD VARIABLES //////////

                double[][] Total_Calves = new double[herdParameter.T][6]; // Store total male and female calves per t,
                double[][] Total_Exp = new double[herdParameter.T][1]; // Store total fixed and other costs

                // NPV of fales of male calves, and NPV of voluntary culled Calves (over limit)
                // DeathsHerd= zeros(1,colherd);
                // DeathsHerd= zeros(T,size(herd,2));
                //finherd= zeros(T,1);
                double[][] N_total_Time = new double[herdParameter.T][24]; //zeros(T,21) Store number of infected animals (MAP, Tb)
                double[][] NumCulledCows = new double[herdParameter.T][4];
                int[][] NumDeath = new int[herdParameter.T][14];
                double[][] NPV = new double[herdParameter.T][8]; // NPV of Milk Production, Culled Cows, Male Calves, Female Calves
                // Population dynamics
                int[][] DeadAnimals = new int[herdParameter.T][14]; // Number of culled calves and heifers

                // Average Parity and Culling Rate
                double[][] avParity = new double[herdParameter.T][2];

                // TB Matrices, Initialization of variables
                int numtestWHT = 0; // Number of WHT Tests
                int[][] WHTTrigger = new int[40][12]; // Whole Herd Test Matrix: Time, WHT number, WHT positive results (1/0)
                int Quarantine = 0;
                int NegWHT = 0; // Numbers of Negative WHT
                int ConsNegWHT = 0; // Number of consecutive Negative WHT
                // USDAProtocol= 1; // Follow the Tb eradication Protocol of the USDA

                int numPrimInfected = 0; // initialize the number of primary infections.

                int[][] numNewbTB = new int[1][3]; // Store the number of newly infected bTB per day (cows, heifers, calves)
                int[][] TBHist = new int[1][8]; // Store all animals not bTB Susceptible: ID, time Occult, time Infective, time death.
                // ID, time Occult Calf (2), tICalf (3), tOH (4), tIH (5), tOC (6), tIC (7), time dead
                for (int t = 1; t <= herdParameter.T; t++) {
                    System.out.println("Today is " + t);
                    double A[] = new double[9];
                    // SHOULD IT BE 1956!! NO GENERALLY ENOUGH
                    if(t == 7300){
                        break;
                    }
                    for (int i = 0; i < herdHelperFunc.getSize() ; i++) {
                        //ROUND OFF ERROR???
                        A[(int) (herd[i][3] - 1)]++;
                    }
                    herdParameter.Si = A[0];
                    herdParameter.Li = A[1];
                    herdParameter.Y1_i = A[2];
                    herdParameter.Y2_i = A[3];
                    herdParameter.CS_i = A[4];
                    herdParameter.CI_i = A[5]; // Infected Calves
                    herdParameter.HS_i = A[6]; // Susceptible Heifers
                    herdParameter.HI_i = A[7]; // Infected Heifers
                    double N_total = herdParameter.Si + herdParameter.Li + herdParameter.Y1_i + herdParameter.Y2_i;
                    // Group cows depending on their lactation:1 dry, 2 early, 3 mid, 4 late lactation
                    int L[] = new int[4];
                    double Y1_i_dry=0; // Number of Y1 in Dry lact stage
                    double Y2_i_dry=0; // Number of Y2 in Dry lact stage
                    double Y1_i_early=0; // Number of Y1 in early lact stage
                    double Y2_i_early=0; // Number of Y2 in early lact stage
                    double Y1_i_mid=0; // Number of Y1 in mid lact stage
                    double Y2_i_mid=0; // Number of Y2 in mid lact stage
                    double Y1_i_late=0; // Number of Y1 in late lact stage
                    double Y2_i_late=0; // Number of Y2 in late lact stage
                    if (herdParameter.groupLact == 1) { // if grouping by lactation stage
                        for (int ii = 0; ii < herdHelperFunc.getSize() ; ii++) {
                            if (herd[ii][5] > 0.0 && herd[ii][9] == 0.0) {
                                herd[ii][41] = 1;
                            } else if (herd[ii][5] > 0.0 && herd[ii][9] > 0.0 && herd[ii][9] < 120.0) {
                                herd[ii][41] = 2;
                            } else if (herd[ii][5] > 0.0 && herd[ii][9] >= 120.0 && herd[ii][9] < 210.0) {
                                herd[ii][41] = 3;
                            } else if (herd[ii][5] > 0.0 && herd[ii][9] >= 210.0) {
                                herd[ii][41] = 4;
                            }
                        }

                        for (int ii = 0; ii < herdHelperFunc.getSize() ; ii++) {
                            if (herd[ii][3] == 3 && herd[ii][41] == 1) {
                                Y1_i_dry++;
                            }
                            if (herd[ii][3] == 4 && herd[ii][41] == 1) {
                                Y2_i_dry++;
                            }
                            if (herd[ii][3] == 3 && herd[ii][41] == 2) {
                                Y1_i_early++;
                            }
                            if (herd[ii][3] == 4 && herd[ii][41] == 2) {
                                Y2_i_early++;
                            }
                            if (herd[ii][3] == 3 && herd[ii][41] == 3) {
                                Y1_i_mid++;
                            }
                            if (herd[ii][3] == 4 && herd[ii][41] == 3) {
                                Y2_i_mid++;
                            }
                            if (herd[ii][3] == 3 && herd[ii][41] == 4) {
                                Y1_i_late++;
                            }
                            if (herd[ii][3] == 4 && herd[ii][41] == 4) {
                                Y2_i_late++;
                            }
                            if(herd[ii][41]!=0){
                                L[(int) herd[ii][41] - 1]++;
                            }
                        }
                    }
                    double S_Inf_AA;
                    double S_Inf_AArate;
                    double S_Inf_AA_dry;
                    double S_Inf_AArate_dry;
                    double S_Inf_AA_early;
                    double S_Inf_AArate_early;
                    double S_Inf_AA_mid;
                    double S_Inf_AArate_mid;
                    double S_Inf_AA_late;
                    double S_Inf_AArate_late;
                    if (herdParameter.groupLact == 0) { // if group all cows into one group
                        if (N_total == 0) {// Make sure result is not NAN.
                            S_Inf_AArate = 0; // MAP
                            double ratio = -S_Inf_AArate / 365;
                            S_Inf_AA = 1 - Math.pow(Math.E,ratio ); // MAP Infection Probability per day
                        } else {
                            // MAP
                            S_Inf_AArate = herdParameter.FactorAA * herdParameter.beta_aa * (herdParameter.beta_y1 * herdParameter.Y1_i + herdParameter.beta_y2 * herdParameter.Y2_i) / N_total; // for adult Mamun's Model CODE, cows are less likely to get infected
                            // Infection Probability
                            double ratio =-S_Inf_AArate / 365;
                            S_Inf_AA = 1 - Math.pow(Math.E, ratio); // Infection Probability per day
                        }
                    } else {
                        if (L[0] == 0) {// Make sure there is no NAN, dry period.
                            S_Inf_AA_dry = 0; // Infection Probability per day
                        } else { // if there are cows in dry stage
                            // MAP
                            S_Inf_AArate_dry = herdParameter.FactorAA * herdParameter.beta_aa * (herdParameter.beta_y1 * Y1_i_dry + herdParameter.beta_y2 * Y2_i_dry) / L[0]; //
                            // Infection Probability
                            S_Inf_AA_dry = 1 - Math.pow(Math.E, -S_Inf_AArate_dry / 365); // Infection Probability per day
                        }

                        if (L[1] == 0) {// Make sure there is no NAN, early lactation.
                            S_Inf_AA_early = 0; // MAP Infection Probability per day
                        } else { // if there are cows in early lactation
                            // MAP
                            S_Inf_AArate_early = herdParameter.FactorAA * herdParameter.beta_aa * (herdParameter.beta_y1 * Y1_i_early + herdParameter.beta_y2 * Y2_i_early) / L[1]; //
                            // Infection Probability
                            S_Inf_AA_early = 1 - Math.pow(Math.E, -S_Inf_AArate_early / 365); // Infection Probability per day
                        }

                        if (L[2] == 0) { // Make sure there is no NAN, mid lactation.
                            S_Inf_AA_mid = 0; // MAP Infection Probability per day
                        } else { // if there are cows in mid lactation
                            // MAP
                            S_Inf_AArate_mid = herdParameter.FactorAA * herdParameter.beta_aa * (herdParameter.beta_y1 * Y1_i_mid + herdParameter.beta_y2 * Y2_i_mid) / L[2]; //
                            // Infection Probability
                            S_Inf_AA_mid = 1 - Math.pow(Math.E, -S_Inf_AArate_mid / 365); // Infection Probability per day
                        }

                        if (L[3] == 0) {// Make sure there is no NAN, late lactation.
                            S_Inf_AA_late = 0; // Infection Probability per day
                        } else { // if there are cows in late lactation
                            // MAP
                            S_Inf_AArate_late = herdParameter.FactorAA * herdParameter.beta_aa * (herdParameter.beta_y1 * Y1_i_late + herdParameter.beta_y2 * Y2_i_late) / L[2]; //
                            // Infection Probability
                            S_Inf_AA_late = 1 - Math.pow(Math.E, -S_Inf_AArate_late / 365); // Infection Probability per day
                        }

                    }
                    // Infection RATE MAP
                    double S_Inf_AC = 0;
                    double S_Inf_ACrate;
                    double S_Inf_AC_dry;
                    double S_Inf_ACrate_dry;
                    double S_Inf_AC_early;
                    double S_Inf_ACrate_early;
                    double S_Inf_AC_mid;
                    double S_Inf_ACrate_mid;
                    double S_Inf_AC_late;
                    double S_Inf_ACrate_late;
                    if (herdParameter.AdultCalfHoriz == 1) { // Allow Horiz Transmission Adult-Calf. This happens only once, when the calf is born. 1 day chance.
                        // First function is from the Paper.
                        // Infection Prob. Adult-Calf (fecal-oral) Maternity area (1 day) PAPER
                        // A-C Infection RATE
                        if (herdParameter.groupLact == 0) {// if group all cows into one group
                            if (N_total > 0) {
                                S_Inf_ACrate = herdParameter.FactorAC * herdParameter.beta_ac * (herdParameter.beta_y1 * herdParameter.Y1_i + herdParameter.beta_y2 * herdParameter.Y2_i) / N_total;
                                // A-C Infection Probability per day
                                if (t >= 1) { //WarmUp
                                    double ratio = -S_Inf_ACrate / 365;
                                    S_Inf_AC = 1 - Math.pow(Math.E, ratio);
//                                    System.out.println(S_Inf_AC);
                                } else {
                                    S_Inf_AC = 0.0425; // Fixed Probability
                                }
                            } else {
                                S_Inf_AC = 0;
                            }

                        } else { // group cows by lactation stage

                            if (L[0] == 0) { // Make sure there is no NAN, dry period.
                                //S_Inf_AA_dry= 0;
                                S_Inf_AC_dry = 0; // Infection Probability per day
                            } else { // if there are cows in dry stage

                                S_Inf_ACrate_dry = herdParameter.FactorAC * herdParameter.beta_ac * (herdParameter.beta_y1 * Y1_i_dry + herdParameter.beta_y2 * Y2_i_dry) / L[0]; //
                                // Infection Probability
                                S_Inf_AC_dry = 1 - Math.pow(Math.E, -S_Inf_ACrate_dry / 365); // Infection Probability per day
                            }

                            if (L[1] == 0) { // Make sure there is no NAN, dry period.
                                //S_Inf_AA_early= 0;
                                S_Inf_AC_early = 0; // Infection Probability per day
                            } else { // if there are cows in dry stage

                                S_Inf_ACrate_early = herdParameter.FactorAC * herdParameter.beta_ac * (herdParameter.beta_y1 * Y1_i_early + herdParameter.beta_y2 * Y2_i_early) / L[1]; //
                                // Infection Probability
                                S_Inf_AC_early = 1 - Math.pow(Math.E, -S_Inf_ACrate_early / 365); // Infection Probability per day
                            }

                            if (L[2] == 0) {// Make sure there is no NAN, dry period.
                                //S_Inf_AA_early= 0;
                                S_Inf_AC_mid = 0; // Infection Probability per day
                            } else { // if there are cows in dry stage

                                S_Inf_ACrate_mid = herdParameter.FactorAC * herdParameter.beta_ac * (herdParameter.beta_y1 * Y1_i_mid + herdParameter.beta_y2 * Y2_i_mid) / L[2]; //
                                // Infection Probability
                                S_Inf_AC_mid = 1 - Math.pow(Math.E, -S_Inf_ACrate_mid / 365); // Infection Probability per day
                            }

                            if (L[3] == 0) { // Make sure there is no NAN, dry period.
                                //S_Inf_AA_early= 0;
                                S_Inf_AC_late = 0; // Infection Probability per day
                            } else { // if there are cows in dry stage

                                S_Inf_ACrate_late = herdParameter.FactorAC * herdParameter.beta_ac * (herdParameter.beta_y1 * Y1_i_late + herdParameter.beta_y2 * Y2_i_late) / L[3]; //
                                // Infection Probability
                                S_Inf_AC_late = 1 - Math.pow(Math.E, -S_Inf_ACrate_late / 365); // Infection Probability per day
                            }
                        }
                    }
                    double C_Inf = 0;
                    if (herdParameter.CalfCalfHoriz == 1) {
                        if (herdParameter.CI_i + herdParameter.CS_i == 0) {
                            // If no Calves
                            C_Inf = 0; // Set to 0 if NAN

                        } else {
                            if (t >= 1) { // WarmUp
                                herdParameter.C_Infrate = herdParameter.FactorCC * herdParameter.beta_c * herdParameter.CI_i / (herdParameter.CI_i + herdParameter.CS_i); // Infection Rate Calf-Calf (fecal-oral) calf rearing area, 60 days.
                                double ratio = -herdParameter.C_Infrate / 365;
                                C_Inf = 1 - Math.pow(Math.E, ratio); // Infection Probability Calf-Calf (fecal-oral) calf rearing area, 60 days.
                            } else {
                                C_Inf = 0.0065; // Fix Probability (this value is from MM, however, here this will never occur. can omit this line.
                            }
                        }
                    }
                    if (herdParameter.HeiferHoriz == 1) {
                        if (herdParameter.HI_i + herdParameter.HS_i == 0) { // If no Heifers
                            herdParameter.H_Inf = 0; // Set to 0 if NAN
                        } else {
                            herdParameter.H_Infrate = herdParameter.FactorHH * herdParameter.beta_h * herdParameter.HI_i / (herdParameter.HI_i + herdParameter.HS_i); // Infection Prob Heifer-Heifer (fecal-oral) Heifer rearing area, 61-719 days of age
                            herdParameter.H_Inf = 1 - Math.pow(Math.E, -herdParameter.H_Infrate / 365); // Infection Prob Heifer-Heifer (fecal-oral) Heifer rearing area, 61-719 days of age
                        }
                    } else {
                        herdParameter.H_Inf = 0;
                    }
                    N_total_Time[t][0] = N_total;
                    N_total_Time[t][1] = herdParameter.Si;
                    N_total_Time[t][2] = herdParameter.Li;
                    N_total_Time[t][3] = herdParameter.Y1_i;
                    N_total_Time[t][4] = herdParameter.Y2_i;
                    N_total_Time[t][5] = herdParameter.CS_i;
                    N_total_Time[t][6] = herdParameter.CI_i;
                    N_total_Time[t][7] = herdParameter.HS_i;
                    N_total_Time[t][8] = herdParameter.HI_i;
                    int num_male = 0; // Counter of number of Male Calves born
                    int num_calf = 0; // Counter of number of Female Calves born

                    //1956 is the size of herd
                    int size = herdHelperFunc.getSize();
                    ArrayList<Thread> arrThreads = new ArrayList<Thread>();
                    for (int i = 0; i < size; i++) {
                        threadHerd singleThread = new threadHerd(herd, i, t,num_male, num_calf, S_Inf_AA_dry,  S_Inf_AA_early,
                                S_Inf_AA_mid, S_Inf_AA_late, S_Inf_AC_dry, S_Inf_AC_early, S_Inf_AC_mid,
                                S_Inf_AC_late, InsemCost, PregnancyDiagnosis, C_Inf, maxMAPDays , MatureWeight,
                                HighShedFeedCostFactorNoMilk, HighShedFeedCostFactorMilkProd, FixedVarsCostDay, CostCalf,
                                id, S_Inf_AC);
                        singleThread.start();
                        arrThreads.add(singleThread);
                    }
                    for (int i = 0; i < arrThreads.size(); i++) {
                        arrThreads.get(i).join();
                    }
                    System.out.println("All collect!");
                    ////// End of the Herd Loop ////////

                    /*
                    //// VOLUNTARY CULLING, INCLUDING FOR MAP CONTROL STRATEGIES
                    THESE VOULUNTARY CULLING AND CONTROL STRATEGIES ARE DONE ONCE THE
                    HERD LOOP IS OVER.
                    //// Voluntary Culling for calves if above calf compartment size limit
                    */

                    if (herdParameter.VoluntaryCalfCull== 1) { // &&  t>= WarmUp // If using voluntary culling for calves
                        int numCalves = 0;
                        double [][]IdAgeC;

                        for (int i = 0; i < herdHelperFunc.getSize(); i++){
                            if(herd[i][3]==5||herd[i][3]==6){
                                numCalves++;
                            }
                        }
                        ; // count the number of Calves time t (suscept + infected)
                        if (numCalves > LimitCalves) { // If number of calves is greater than the limit
                            IdAgeC = herdHelperFunc.select_calves_by_age(); // matrix of calf ID and Age,
                            IdAgeC = herdHelperFunc.get_sorted_herd(IdAgeC, 1,0,0, false); // Sort calves by age in descending order (older first)
                            for(int m = herdParameter.herdLimit+1; m < IdAgeC.length; m++){
                                for (int n = 0; n< herdHelperFunc.getSize(); n++){
                                    if (herd[n][1]==IdAgeC[n][1]) {
                                        herd[n][14]=7; // Mark IdElim calves as cull for over limit
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (herdParameter.VoluntCullOption== 1) {// If using VolungCullingOption (upper limit of cows)
                        int numCows = 0;
                        double[][] IdAgeC;

                        // Cull oldest cows above 1000
                        for (int i = 0; i < herdHelperFunc.getSize(); i++) {
                            if (herd[i][3] == 1 || herd[i][3] == 2 || herd[i][3] == 3 || herd[i][3] == 4) {
                                numCows++;
                            }
                        } // Number of Cows

                        if (numCows > herdParameter.herdLimit && herdParameter.allsusceptible == 0) {
                            // if number of cows is greater than the capacity of the herd AND MAP
                            /**
                             * Instead of kill by age, we now kill by value of milkproduction !
                             */
//                            IdAgeC = herdHelperFunc.select_cows_by_age();
                            IdAgeC = herdHelperFunc.select_cows_by_milk_value(numCows - herdParameter.herdLimit );
                            for (int n = 0; n < IdAgeC.length; n++) {
                                if (IdAgeC[n][4] >= IdAgeC[n][3]) {
                                    IdAgeC[n][7] = 1;
                                    IdAgeC[n][8] = IdAgeC[n][3];// If MAP Days >= DIM, place DIM in col.
                                } else {
                                    IdAgeC[n][8] = IdAgeC[n][4]; // If DIM> MAP Days, place MAP Days in col. 8
                                }
                                if (IdAgeC[n][8] >= 1 && IdAgeC[n][2] == 4) {
                                    IdAgeC[n][9] = 1;
                                }
                            }
                            IdAgeC = herdHelperFunc.get_sorted_herd(IdAgeC, 9, 1, 3, true);// Sort cows by Y2 with DIM >= 90, Age, DIM
                            for (int m = herdParameter.herdLimit + 1; m < IdAgeC.length; m++) {
                                for (int n = 0; n < herdHelperFunc.getSize(); n++) {
                                    if (herd[n][1] == IdAgeC[n][1]) {
                                        herd[n][14] = 6; // Mark IdElim calves as cull for over limit
                                        break;
                                    }
                                }
                            }

                        } else if (numCows > herdParameter.herdLimit && herdParameter.allsusceptible == 1) { // if number of cows is greater than the capacity of the herd AND NO MAP
                            IdAgeC = herdHelperFunc.select_susceptible_cows(); // Create new matrix of all cows
                            // matrix columns: 1 cow ID; 2 Age; 3 Parity; 4 Pregnant days;
                            // 5 DIM.
                            IdAgeC = herdHelperFunc.get_sorted_herd(IdAgeC, 7, 2, 3, true);
                            for (int m = herdParameter.herdLimit + 1; m < IdAgeC.length; m++) {
                                for (int n = 0; n < herdHelperFunc.getSize(); n++) {
                                    if (IdAgeC[n][1] == herd[n][1]) {
                                        herd[n][14] = 6; // Mark IdElim calves as cull for over limit
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////////////////////////////

                    // SCENARIOS
                    // MAP DISTRIBUTION AT END OF WARM UP.

                    // After the burn our period (3000 days) randomly change status of cows
                    // from S to Y2, or L and Y1.
                    // ScenatioWarmUp== 0; All Healthy

                    if (herdParameter.ScenarioWarmUp== 1 ) {// Include a number of cows of a given infection status at the beginning of the simulation
                        double [][]herdNew;
                        if (t == herdParameter.WarmUp + 1) {
                            double [][] ListCowS = herdHelperFunc.get_ListCowS(); // Vector of ID, Infection Status, Parity number
                            // Indicaror Vector that meets the condition. Elim if not Susceptible
                            int sum_par = herdParameter.parityScenarioWarmUp[0]+ herdParameter.parityScenarioWarmUp[1]+ herdParameter.parityScenarioWarmUp[2]+ herdParameter.parityScenarioWarmUp[3]+ herdParameter.parityScenarioWarmUp[4];

                            double [] IDRand = herdHelperFunc.data_sample(ListCowS,sum_par,0,0);
                            ; // Select random ID values and number of new Infection Status Cows!!!
                            double idmax = herd[herdHelperFunc.getSize()-1][1]; // get the highest ID number
                            herdHelperFunc.get_rid_of_cow_by_ID(herd, IDRand ,1);// Eliminate random number of cows and include same number of infected cows

                            double[][] age_all = herdHelperFunc.get_age_all(herdParameter.parityScenarioWarmUp);


                            int inf_stat = herdParameter.NewInfectedType; // Infection status of the newly introduced cows
                            herdNew = new double[age_all[0].length][43];  // Initialize HerdNew
                            for(int i =0; i < herdNew.length; i++){

                                double[] row = herdHelperFunc.ini_ageHS(age_all[0][i]);
                                id = idmax+i+1;
                                double [] temp = new double[]{t, id, row[0],inf_stat,row[3], row[1], row[2],row[4],0,row[7]};
                                herdNew[i] = Arrays.copyOf(temp,43);
                                herdNew[i][42] = 5;

                            }
                            herdHelperFunc.append_to_end(herdNew); // Concatenate herdNew to herd

                        }
                        //                    clear idmax age par par_age lac_status preg_days days_left_calving exp_calving_date dim herdNew Locb
                    }
                    else if (herdParameter.ScenarioWarmUp== 2 ) {// A proportion of Cows (PercInfected) are infected (L,Y1,Y2) at warmup.
                        if (t == herdParameter.WarmUp + 1) {
                            double [][]ListCowS = herdHelperFunc.get_ListCowS2();; // Vector of ID, Infection Status, Parity
                            // Elim if not 1-4 (eliminate non Cows)
                            int NumInitialInfect = (int)(ListCowS[0].length * herdParameter.PercInfected); // PercInfected// (10//) of initial Cows will be infected
                            // num cows that are infected (L,Y1,Y2)
                            // Or, Number of cows that change infection status.

                            // From NumInitialInfect (Numbner of cows that change infection
                            // status), first change to Y2 those cows that are in Perity
                            // >=3; then Y1, and then L.

                            // Get a list of Cows that are in parity>= 3 to change to Y2    ListCowS(ListCowS(:,3)>=3, 1)
                            double []IDRandY2 = herdHelperFunc.data_sample(ListCowS , (int)Math.round(NumInitialInfect * herdParameter.ScenarioPercY2),0,3);
                            // Eliminate those cows from the ListCowS
                            herdHelperFunc.get_rid_of_cow_by_ID(ListCowS,IDRandY2,0);

                            // Get a list of Cows that are in parity>= 2 to change to Y1
                            double[] IDRandY1 = herdHelperFunc.data_sample(ListCowS, (int)Math.round(NumInitialInfect * herdParameter.ScenarioPercY1),0, 2);
                            // Eliminate those cows from the ListCowS
                            herdHelperFunc.get_rid_of_cow_by_ID(ListCowS,IDRandY1,0);

                            // Get a list of Cows that are in parity>= 1 to change to L
                            double[] IDRandL = herdHelperFunc.data_sample(ListCowS, (int)Math.round(NumInitialInfect * herdParameter.ScenarioPercL),0, 1);
                            // Eliminate those cows from the ListCowS
                            herdHelperFunc.get_rid_of_cow_by_ID(ListCowS,IDRandL,0);

                            // Change Infection Status in Herd.
                            for(int i =0; i < IDRandY2.length; i++){
                                for(int j = 0; j < herdHelperFunc.getSize(); j++){
                                    if(IDRandY2[i]==-1){
                                        break;
                                    }
                                    if(herd[j][1]==IDRandY2[i]){
                                        herd[j][3]=4;// Change status to Y1
                                    }
                                }
                            }
                            // Change Infection Status in Herd.
                            for(int i =0; i < IDRandY1.length; i++){
                                for(int j = 0; j < herdHelperFunc.getSize(); j++){
                                    if(IDRandY2[i]==-1){
                                        break;
                                    }
                                    if(herd[j][1]==IDRandY2[i]){
                                        herd[j][3]=3;// Change status to Y1
                                    }
                                }
                            }
                            // Change Infection Status in Herd.
                            for(int i =0; i < IDRandL.length; i++){
                                for(int j = 0; j < herdHelperFunc.getSize(); j++){
                                    if(IDRandY2[i]==-1){
                                        break;
                                    }
                                    if(herd[j][1]==IDRandY2[i]){
                                        herd[j][3]=2;// Change status to L
                                    }
                                }
                            }
                        }
                    }
                    herd = herdHelperFunc.clean_up_herd();

                    //////////////////////////////////////////////////////////////////////////////////////////
                    //NPV Calculation for Milk Production, Sale of Culled Cows, and Sale of Male Calves

                    // At this point, herd includes both dead and live animals
                    // We Assume that animals die at the end of the day, so their production
                    // and calving are stored
                    // Estimation of average parity and culling rate (1/parity)
                    if(t >= 4726){
//                        System.out.print(t);
                    }
                    avParity[t][0] = herdHelperFunc.get_mean_of_column(5);// Average Parity of the herd
                    avParity[t][1] = 1/(avParity[t][0]);// average Culling Rate of the herd
                    double[][] DeathsHerd = herdHelperFunc.get_DeathsHerd();

                    DeadAnimals[t] = herdHelperFunc.counter_death(DeadAnimals[t]);

                    herdHelperFunc.gross_profit();
    /*
                    This value is the net profit of each animal (cow, heifer, calf)
                    The costs are feed and other fixed costs (herd:,24 and 23)
                    herd(i,13) is the tests costs
                    The revenue is only milk sold!

                    The revenues of cull cow and other cull animals do not count cost of
                    raising the animal, otherwise we will be double counting.

                    Discount Gross Profit.
                    Select time 0 of the analysis (720 days= 2 years)
    */
                    int StartDay= herdParameter.StabilityTime+ herdParameter.WarmUp+1;
                    int daysInt= (int)herd[0][0]-StartDay; // Interval Days, current day - start day
                    NumDeath[t] = herdHelperFunc.get_NumDeath(NumDeath[t]);
                    NumCulledCows[t][0] =NumDeath[t][3]+NumDeath[t][4]+NumDeath[t][5]+NumDeath[t][8];
                    double TotWeightCullCowsLb = herdHelperFunc.get_TotWeightCullCowsLb();

                    double[] NumCullInf = herdHelperFunc.get_NumCullInf();

                    double NumCCows = NumCullInf[0]+NumCullInf[1]+NumCullInf[2]+NumCullInf[3];
                    double NumY2Cull = NumCullInf[3];
                    double PercCullY2;
                    if (NumCCows== 0) {
                        PercCullY2 = 0;
                    }else {
                        PercCullY2 = NumY2Cull / NumCCows; //Ratio of Y2 of all Culled
                    }

                    // Adjusted Culled Cow's Price for Y2
                    double CullPriceAdjusted= CulledCowPrice*(1-WeightLossY2Perc*PercCullY2);

                    //Calculate the revenues from sales of male calves
                    // Total number of male and female calves. Used to get revenus from sale
                    // of male calves.
                    Total_Calves[t]= new double[]{num_male, num_calf,0,0, DeadAnimals[t][7], 0}; // Number of male and female calves per t

                    if (daysInt>= 0) { // Calculate Discounted Gross Profit and other revenues after WarmUp Period
                        // Whether we are doing exponential on one of items in herd or the whole row of herd??
                        for(int i = 0; i < herdHelperFunc.getSize(); i++){

                            double kks = herd[i][24]/Math.pow((1 + dailyirate),(double)daysInt);// Second Method (BW, DMI)
                            herd[i][25] = kks;
                        }
                        Total_Calves[t][2] = Total_Calves[t][0] * (MaleCalfPrice) / Math.pow((1 + dailyirate), daysInt); // Revenue from sale of male calves
                        Total_Calves[t][3] = DeadAnimals[t][6] * FemaleCalfPrice / Math.pow((1 + dailyirate), daysInt); // Revenue from sale of Female calves due to Over Limit
                        Total_Calves[t][5] = DeadAnimals[t][7] * HeiferReplCost / Math.pow((1 + dailyirate), daysInt); // Revenue from selling pregnant heifers
                        // If culled by open or over parity, and voluntary culling // Calculate Discounted Gross Profit after WarmUp Period
                        NumCulledCows[t][1] = Math.pow((NumCulledCows[t][0] + NumDeath[t][11]) * CullPriceAdjusted / (1 + dailyirate), daysInt);
                        Total_Exp[t][0] = Math.pow(herdHelperFunc.get_sum_col(12) / (1 + dailyirate), daysInt); // Total Management Changes and Interventions Expenses
                    }
                    // Using Function of BW and DMI
                    // Store in the NPV Matrix for each time period
                    NPV[t][0]= herdHelperFunc.get_sum_col(25);     // NPV of net milk revenue of animal per day (using function of BW, DMI)
                    NPV[t][1]= NumCulledCows[t][1];   // NPV of selling culled cows
                    NPV[t][2]= Total_Calves[t][2];    // NPV of Male Calves Sale
                    NPV[t][3]= Total_Calves[t][3];    // NPV of Female Calves Sale
                    NPV[t][7]= Total_Calves[t][5];    // NPV of heifers sold
                    NPV[t][4]= NPV[t][0]+ NPV[t][1]+ NPV[t][2]+ NPV[t][3] + NPV[t][7]; // Sum of Gross Profit NPV Without Intervention Costs
                    NPV[t][5]= Total_Exp[t][0];       // Total Management Changes and Interventions Expenses
                    NPV[t][6]= NPV[t][4]-NPV[t][5];    // Net NPV

                    // Reset variable values
                    for(int i = 0; i < herdHelperFunc.getSize(); i++){
                        herd[i][17]=0; // 1 if Inseminated at time t, 0 otherwise.
                        herd[i][18]=0; // 1 if pregnant at time t, 0 otherwise
                        if(herd[i][14]>0){
                            herd[i][1]=-1;
                        }
                    }
                    // Clean Up the herd
                    // Eliminate the dead animals from the herd matrix
                    herd = herdHelperFunc.clean_up_herd();
                    int countforcal=0, countforhel = 0, countforadl=0, low =0, high=0;
                    int num_cal =0, num_hei =0, num_adu = 0;
                    int [] par_count = new int[8];

                    for(int i = 0; i< herdHelperFunc.getSize(); i++){
                        if(herd[i][2]<=60){
                            num_cal++;
                        }
                        if(herd[i][2]>60 && herd[i][2]<=719){
                            num_hei++;
                        }
                        if(herd[i][2]>=720 ){
                            num_adu++;
                        }
                        if(herd[i][3]==6){
                            countforcal++;
                        }
                        if(herd[i][3]==8){
                            countforhel++;
                        }
                        if(herd[i][3]==2){
                            countforadl++;
                        }
                        if(herd[i][3]==3){
                            low++;
                        }
                        if(herd[i][3]==4){
                            high++;
                        }
                        if(herd[i][5]!=0){
                            par_count[(int)herd[i][5]-1]++;
                        }


                    }
                    writer.print(jj + ",");
                    writer.print(t + ",");
                    writer.print(num_cal + ",");
                    writer.print(num_hei + ",");
                    writer.print(num_adu + ",");
                    writer.print(countforcal + ",");
                    writer.print(countforhel + ",");
                    writer.print(countforadl + ",");
                    writer.print(low + ",");
                    writer.print(high + ",");
                    for (int i =0; i<7; i++){
                        writer.print(par_count[i] + ",");
                    }
                    writer.println(par_count[7]);
                    if(t==7000){
                        for (int i = 0; i< herdHelperFunc.getSize(); i++){
                            for (int j =0; j < 43; j++){
                                System.out.print(herd[i][j] + " ");
                            }
                            System.out.println();
                        }

                        System.out.println("sdf");
                    }
                }
            }
        }
        writer.close();
    }
}
