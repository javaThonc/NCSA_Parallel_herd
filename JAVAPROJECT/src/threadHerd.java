import java.util.Random;

import static java.lang.Math.ceil;

public class threadHerd extends Thread{
    static double [][] herd;
    private int i;
    private int t;
    private int num_male;
    private int num_calf;
    private double  S_Inf_AA_dry;
    private double S_Inf_AA_early;
    private double S_Inf_AA_mid;
    private double S_Inf_AA_late;
    private double S_Inf_AC_dry;
    private double S_Inf_AC_early;
    private double S_Inf_AC_mid;
    private double S_Inf_AC_late;
    private int InsemCost;
    private int PregnancyDiagnosis;
    private double C_Inf;
    private int maxMAPDays;
    private int MatureWeight;
    private double HighShedFeedCostFactorNoMilk;
    private double HighShedFeedCostFactorMilkProd;
    private double FixedVarsCostDay;
    private double CostCalf;
    private double id;
    private double S_Inf_AC;
    private int Beta;

    /**
     * Initial the parameters with the input constructor and prepare for use in
     * @param herd
     * @param i
     * @param t
     * @param num_male
     * @param num_calf
     * @param S_Inf_AA_dry
     * @param S_Inf_AA_early
     * @param S_Inf_AA_mid
     * @param S_Inf_AA_late
     * @param S_Inf_AC_dry
     * @param S_Inf_AC_early
     * @param S_Inf_AC_mid
     * @param S_Inf_AC_late
     * @param InsemCost
     * @param PregnancyDiagnosis
     * @param C_Inf
     * @param maxMAPDays
     * @param MatureWeight
     * @param HighShedFeedCostFactorNoMilk
     * @param HighShedFeedCostFactorMilkProd
     * @param FixedVarsCostDay
     * @param CostCalf
     * @param id
     * @param S_Inf_AC
     */
    threadHerd(double [][] herd, int i, int  t, int num_male, int num_calf, double  S_Inf_AA_dry, double S_Inf_AA_early,
                      double S_Inf_AA_mid, double S_Inf_AA_late, double S_Inf_AC_dry, double S_Inf_AC_early, double S_Inf_AC_mid,
                      double S_Inf_AC_late, int InsemCost, int PregnancyDiagnosis, double C_Inf, int maxMAPDays , int MatureWeight,
                      double HighShedFeedCostFactorNoMilk, double HighShedFeedCostFactorMilkProd, double FixedVarsCostDay, double CostCalf,
                      double id, double S_Inf_AC, int Beta){
        this.herd = herd;
        this.i=i;
        this.t=t;
        this.num_male= num_male;
        this.num_calf = num_calf;
        this.S_Inf_AA_dry= S_Inf_AA_dry;
        this.S_Inf_AA_early = S_Inf_AA_early;
        this.S_Inf_AA_mid = S_Inf_AA_mid;
        this.S_Inf_AA_late = S_Inf_AA_late;
        this.S_Inf_AC_dry = S_Inf_AC_dry;
        this.S_Inf_AC_early = S_Inf_AC_early;
        this.S_Inf_AC_mid = S_Inf_AC_mid;
        this.S_Inf_AC_late = S_Inf_AC_late;
        this.InsemCost = InsemCost;
        this.PregnancyDiagnosis =PregnancyDiagnosis;
        this.C_Inf =C_Inf;
        this.maxMAPDays =maxMAPDays;
        this.MatureWeight =MatureWeight;
        this.HighShedFeedCostFactorNoMilk =HighShedFeedCostFactorNoMilk;
        this.HighShedFeedCostFactorMilkProd =HighShedFeedCostFactorMilkProd;
        this.FixedVarsCostDay =FixedVarsCostDay;
        this.CostCalf = CostCalf;
        this.id =id;
        this.S_Inf_AC= S_Inf_AC;
        this.Beta = Beta;
    }

    public void run(){
        //////// Count the number of infected calves per day.
        Thread currentThread = Thread.currentThread();
//        System.out.println("Executing  thread : " + currentThread.getId()) ;
        int Num_pregnant = 0;
        int Num_Heifers = 0; // Counter of number of Heifers
        int calf_S = 0; // Counter of number of calves born from Susceptible Cows
        int calf_L = 0; // Counter of number of calves born from Latent Cows
        int calf_Y1 = 0, calf_Y2 = 0; // counter of num of calves born from Y1 and Y2
        int Inf_Heifer = 0; // Num of Horizontally infected Heifers
        int Num_InfCalves = 0; // Num of Horizontally infected calves
        int non_pregnant = 0; // Counter of non-pregnancies
        int TransS_L = 0; // Number of Newly infected cows (from Susceptible to Latent)
        int TransL_Y1 = 0; // Progression of the Infection from L to Y1
        int TransY1_Y2 = 0; // Progression of the Infection from Y1 to Y2
        int vertC_L = 0, vertC_Y1 = 0, vertC_Y2 = 0; // Number of Vertically infected calves, and infection status of cow.
        int hori_calf = 0; // Number of Horizontally Infected calves
        herd[i][0] = t;
        herd[i][2] = herd[i][2] + 1;
        // same, only depends on parity number
        if (herd[i][3] == 1 || herd[i][3] == 2 || herd[i][3] == 3 || herd[i][3] == 4) {
            double x = Math.random(); // Get a random number U[0,1] Involuntary culling
            double death_prob = herdHelperFunc.nat_cull_prob((int) herd[i][5], herdParameter.factorcull); //nat_cull_prob_same; //nat_cull_prob(herd[i][5]); // Same Probability to all Parities
            if (x <= death_prob) {//natCullCow // if the random draw is < natural cull prob
                herd[i][14] = 1; // Nat. Culling indicator in the herd matrix
            }
        } else if (herd[i][3] == 5 || herd[i][3] == 6) {
            double x = Math.random(); // Get a random number U[0,1] Involuntary culling
            if (x <= herdParameter.NatCullProbCalves) { //0.000410 //MM  NatCullProbCalves; //PAPER if the random draw is < natural cull prob Calves
                herd[i][14] = 2; // Nat. Culling indicator in the herd matrix
            }
        } else if (herd[i][3] == 7 || herd[i][3] == 8) {
            double x = Math.random(); // Get a random number U[0,1] Involuntary culling
            if (x <= herdParameter.NatCullProbHeifers) { //0.000410 //MM  NatCullProbCalves; //PAPER if the random draw is < natural cull prob Calves
                herd[i][14] = 3; // Nat. Culling indicator in the herd matrix
            }
        }


        if (herdParameter.volculloption == 1) { // Continue with voluntary Cull Option
            if (herd[i][14] == 0 && (herd[i][3] == 1 || herd[i][3] == 2 || herd[i][3] == 3 || herd[i][4] == 4)) {
                double x = Math.random(); // Get a random number U[0,1] voluntary culling
                double death_prob = herdParameter.vol_cull_prob_same; //nat_cull_prob(herd[i][5]); // Same Probability to all Parities
                if (x <= death_prob) { //natCullCow // if the random draw is < natural cull prob
                    herd[i][14] = 9; // Nat. Culling indicator in the herd matrix
                }

                // If Calves. Infection Status is 5,6 (SC,IC)
            } else if (herd[i][14] == 0 && (herd[i][14] == 5 || herd[i][14] == 6)) { // if herd(i,3) is calf
                double x = Math.random(); // Get a random number U[0,1] voluntary culling
                if (x <= herdParameter.VolCullProbCalves) {// NatCullProbCalves
                    herd[i][14] = 10; // Nat. Culling indicator in the herd matrix
                }
            }

            // If Heifers. Infection Status is 7,8 (SH,IH)
            else if (herd[i][14] == 0 && (herd[i][14] == 7 || herd[i][14] == 8)) {// if herd(i,3) is heifer
                double x = Math.random(); // Get a random number U[0,1] voluntary culling
                if (x <= herdParameter.VolCullProbHeifers) {//
                    herd[i][14] = 11; // Nat. Culling indicator in the herd matrix
                }
            } // end of if x< NatCullProbHeifers

        } // end of if herd[i][3]== 1, etc...

        // Dead animals exit herd loop here

        //////////////// Management (Voluntary) Culling from other diseases/accidents is Done

        //////////////// At this point both Natural and voluntary culling are
        //////////////// completed

        ////// 3. Estimate the Transition Probabilities from a Lower Infection
        ////// to a Higher Infection Level
        double S_Inf_AA;
        double S_L = 0, L_Y1 = 0, Y1_Y2 = 0, infection; // set infection results to 0 (initialize)
        if (herdParameter.AllowInfection == 1) { // 1/0 0= Do Not allow any type of Infection. Used to measure the baseline with no externalities
            if (herd[i][3] == 1) { // If Susceptible, or Infection status== 1 && Allow Susc Inf=1
                if (herdParameter.groupLact == 1) {
                    if (herd[i][41] == 1) {
                        S_Inf_AA = S_Inf_AA_dry;
                    } else if (herd[i][41] == 2) {
                        S_Inf_AA = S_Inf_AA_early;
                    } else if (herd[i][41] == 3) {
                        S_Inf_AA = S_Inf_AA_mid;
                    } else if (herd[i][41] == 4) {
                        S_Inf_AA = S_Inf_AA_late;
                    }
                }

                if (herdParameter.HorTransSusc == 1) { // Horizontal Transmission among cows S-L
                    S_L = herdHelperFunc.infection_chance(S_Inf_AA); // S_L = (1,2)
                    herd[i][3] = S_L;
                }
            } else if (herd[i][3] == 2) { // If Latent, status == 2, progress to Y1
                double[] temp =
                        herdHelperFunc.exit_L_Y1HS(herdParameter.FactorProgtoY1 * herdParameter.ExitRateLtoY1); // Run function to simulate fecal-oral transmission to E. E_Y1 can be 2 or 3.
                L_Y1 = temp[0];
                infection = temp[1];
                herd[i][3] = L_Y1; // update infection level (either E or Y1). in funct, L_Y1 = 2,3
            } else if (herd[i][3] == 3) {// If Low Shedder, status== 3, progress to Y2
                double[] temp =
                        herdHelperFunc.exit_Y1_Y2HS(herdParameter.FactorProgtoY2 * herdParameter.ExitRateY1toY2); // Run function to simulate fecal-oral transmission to Y1. Y1-Y2 can be 3 or 4
                Y1_Y2 = temp[0];
                infection = temp[1];
                herd[i][3] = Y1_Y2; // update infection level (either Y1 or Y2), in funct, Y1_Y2= 3,4
            } //else if (herd[i][3] == 4) {// If High Shedder, status== 4, stay in Y2
            // Do nothing, this is the maximum infection level

        }     //else { // If not 1,2,3,4, then exit this conditions

        //} // end of herd[i][3], Transition Prob to higher Infection Level
        double VerticalInf = 0;
        if (herdParameter.VerticalTrans == 1) { // Allow Vertical In Utero Transmission.
            // For S= 0; vertical_L= 15//; vertical_Y1= 15//; vertical_Y2= 17//
            if (herd[i][3] == 1) {// Susceptible
                VerticalInf = 0;
            } else if (herd[i][3] == 2) {// Latent
                VerticalInf = herdParameter.vertical_L * herdParameter.FactorVertical_L;
            } else if (herd[i][3] == 3) {// Low Shedders
                VerticalInf = herdParameter.vertical_Y1 * herdParameter.FactorVertical_Y1;
            } else if (herd[i][3] == 4) {// High Shedders
                VerticalInf = herdParameter.vertical_Y2 * herdParameter.FactorVertical_Y2;
            }

        } else {
            VerticalInf = 0;
        }
        ////// 4. Estimate Days in new MAP infection level.
        if (herd[i][3] == 2 || herd[i][3] == 3 || herd[i][3] == 4) {
            herd[i][10] = herd[i][10] + 1; // If MAP infected, increase MAP days by 1.
        }
        if (S_L == 2 || L_Y1 == 3 || Y1_Y2 == 4) {// if change to new infection level
            herd[i][10] = 1; // save the day of changing infection level   MAPDays

        }

        if (S_L == 2) { // if change to new infection level
            TransS_L = TransS_L + 1; // Increse the number of Horizontally Infected Latent Cows.
        } else if (L_Y1 == 3) {
            TransL_Y1 = TransL_Y1 + 1; // Increse the number of Cows progressiong to Y1.
        } else if (Y1_Y2 == 4) {
            TransY1_Y2 = TransY1_Y2 + 1; // Increse the number of Cows progressiong to Y2.

        }
        double infect_calf;

        if (herd[i][4] == 2) { // If Pregnant or Calving now
            if (herd[i][7] == 280) { // Calving now!
                // randomly select gender of the calf 1= male, 2= female
                Random generator = new Random();
                int gen = generator.nextInt(2) + 1; // random integer [1,2]


                if (gen == 1) { // If male calf
                    num_male = num_male + 1; // count number of male calves
                } else {// if not male, or female
                    num_calf = num_calf + 1; // Count number of female calves
                    id = id + 1; // increase the number of ID, assign it to this calf
                    herd[i][32] = id; // calf ID in cow's data
                    if (herdParameter.groupLact == 1) { // if lactation stage, modify A-C trans

                        if (herd[i][41] == 1) {
                            S_Inf_AC = S_Inf_AC_dry;

                        } else if (herd[i][41] == 2) {
                            S_Inf_AC = S_Inf_AC_early;

                        } else if (herd[i][41] == 3) {
                            S_Inf_AC = S_Inf_AC_mid;

                        } else if (herd[i][41] == 4) {
                            S_Inf_AC = S_Inf_AC_late;
                        }
                    }
                    // COWS Infection Status and Calve-Adult MAP
                    // Transmission
                    if (herd[i][3] == 1) { // If Susceptible Cow
                        calf_S = calf_S + 1; // Count number of calves from Suscept cows
                        double x = Math.random(); // random num (0,1)

                        infect_calf = 5; // Susceptible Calf

                        if (x < S_Inf_AC) { //S_inf_chance_calves// MM. S_Inf_AC; // PAPER // Horiz Trans Adult-Calf. S_inf_chance_calves; // horizontal transmissio for calf
                            infect_calf = 6; // Infected Calf
                            hori_calf = hori_calf + 1; // Counter of horizontally infected calves
                        } // end conditional horizontal transmission

                    } else if (herd[i][3] == 2) {// If Latent Cow
                        calf_L = calf_L + 1; // Count number of calves from Latent cows
                        double x = Math.random(); // random num (0,1)
                        double x1 = Math.random(); // Independent random num from x
                        infect_calf = 5; // Susceptible Calf from Latent Cow
                        if (x < VerticalInf) {// Vertical transmission from Latent Cow to calf
                            infect_calf = 6; // Infected Calf
                            vertC_L = vertC_L + 1; // Count number of Vert. Inf. Calves from L

                        } else if (x1 < S_Inf_AC) { //S_inf_chance_calves //MM. //S_Inf_AC; PAPER// Horiz Trans Adult-Calf. S_inf_chance_calves; // horizontal transmissio for calf, only if not transmited vertically
                            infect_calf = 6; // Infected Calf
                            hori_calf = hori_calf + 1; // Counter of horizontally infected calves
                        } // end conditional horizontal transmission

                    } else if (herd[i][3] == 3) {// If Low Shedder Cow
                        calf_Y1 = calf_Y1 + 1; // Count number of calves from Latent cows
                        double x = Math.random(); // random num (0,1)
                        double x1 = Math.random(); // Independent random num from x
                        infect_calf = 5; // Susceptible Calf from Low Shedder Cow
                        if (x < VerticalInf) { // Vertical transmission from Low Shedder Cow to calf
                            infect_calf = 6; // Infected Calf
                            vertC_Y1 = vertC_Y1 + 1; // Count number of Vert. Inf. Calves from Y1

                        } else if (x1 < S_Inf_AC) {//S_inf_chance_calves  // Horiz Trans Adult-Calf. S_inf_chance_calves; // horizontal transmissio for calf, only if not transmited vertically
                            infect_calf = 6; // Infected Calf
                            hori_calf = hori_calf + 1; // Counter of horizontally infected calves
                        } // end conditional horizontal transmission

                    } else if (herd[i][3] == 4) {// If High Shedder Cow
                        calf_Y2 = calf_Y2 + 1; // Count number of calves from Latent cows
                        double x = Math.random(); // random num (0,1)
                        double x1 = Math.random(); // Independent random num from x
                        infect_calf = 5; // Susceptible Calf from Latent Cow
                        if (x < VerticalInf) {// Vertical transmission from High Shedder Cow to calf
                            infect_calf = 6; // Infected Calf
                            vertC_Y2 = vertC_Y2 + 1; // Count number of Vert. Inf. Calves from Y2

                        } else if (x1 < S_Inf_AC) {//S_inf_chance Horiz trans Adult-calf. S_inf_chance_calves; // horizontal transmissio for calf, only if not transmited vertically
                            // SHOULD BE S_inf_chance_calves
                            infect_calf = 6; // Infected Calf
                            hori_calf = hori_calf + 1; // Counter of horizontally infected calves
                        } // end conditional horizontal transmission
                    } else {
                        infect_calf = 5; // Susceptible Calf
                    } // end conditionals on Cow Infection Status
                    int age = 0;
                    int par = 0;
                    int lac_status = 0;
                    int par_age = 0;
                    int preg_days = 0;
                    int dim = 0;
                    double momID = herd[i][1]; // Id of the mother of the newborn calf
                    // Place the female calves in the Herd Matrix (end of the matrix)
                    // CALVES. All Newborn Calves are Tb Susceptible (5), unless
                    // infected at birth
                    herdHelperFunc.push_cow(t, id, age, infect_calf, lac_status, par, par_age, preg_days, dim, momID);

                } // end calf information conditional statements

                // Update Cow's State after calving
                herd[i][4] = 3; // Lactation Status set to VWP (= 3)
                herd[i][5] = herd[i][5] + 1; // Update Parity Number
                herd[i][6] = herd[i][2]; // Update the Parity Age. This is the reference age to inseminate
                herd[i][7] = 0; // Pregnancy Days= 0,
                herd[i][9] = 0; // Days in Milk set to 0, since it has just calved

                if (herd[i][3] == 7) {// If susceptible heifer
                    herd[i][3] = 1; // Becomes Susceptible Cow
                } else if (herd[i][3] == 8) { // If Infected Heifer
                    herd[i][3] = 2; //Becomes Occult Cow

                }
            } else { // when pregnant BUT not calving now
                herd[i][7] = herd[i][7] + 1; // Pregnancy Days + 1

                if (herd[i][5] > 0 && herd[i][7] > 0 && herd[i][7] < 220) { // Pregnant not heifer, and not at Dry period
                    herd[i][9] = herd[i][9] + 1; // Increase DIM by 1
                } else if (herd[i][5] > 0 && herd[i][7] >= 220) { // if at Dry Period
                    herd[i][9] = 0; // Do not milk
                }
            }

            // end conditional stat. on calving now (pregnant days)

        } else if (herd[i][4] == 3 && herd[i][5] > 0) {// If VWP
            herd[i][9] = herd[i][9] + 1; // Increase DIM by 1

            if (herd[i][2] == herd[i][6] + 60) { // if over the 60 days or VWP
                herd[i][4] = 1; // change status from VWP to ready to inseminate (1)
            } // end of conditional to exit VWP

            // Inseminate cows that are under maximum parity
        } else if (herd[i][4] == 1 && herd[i][6] >= herdParameter.MaxParity && (herd[i][3] != 7 && herd[i][3] != 8)) {// if ready to be inseminated. Not Heifers, nor cows that will be culled
            if (herd[i][30] == 0) { // IF cow is not going to be culled

                // INSEMINATION OF COWS
                // Inseminate at the end of VWP and every 21 days after it

                if (herd[i][2] == herd[i][6] + 61 || herd[i][2] == herd[i][6] + 82 || herd[i][2] == herd[i][6] + 103
                        || herd[i][2] == herd[i][6] + 124 || herd[i][2] == herd[i][6] + 145 || herd[i][2] == herd[i][6] + 166
                        || herd[i][2] == herd[i][6] + 187 || herd[i][2] == herd[i][6] + 208 || herd[i][2] == herd[i][6] + 229
                        || herd[i][2] == herd[i][6] + 250 || herd[i][2] == herd[i][6] + 271 || herd[i][2] == herd[i][6] + 292) {


                    double par_status = herd[i][5]; // Parity number
                    double days_in_milk = herd[i][9]; // DIM
                    double prob_preg = herdHelperFunc.prob_insi_suc70((int) par_status, herdParameter.prob_pregInput, herdParameter.annualProb, herdParameter.AIattempts, herdParameter.MaxParity); // Function to estimate AI Success Prob.
                    double x = Math.random(); // random number
                    herd[i][17] = herd[i][0]; // Time of AI
                    double factorPregY2;
                    if (herd[i][3] == 4) { // If High Shedder, derease Pregnancy rate by PregRateDecreaseY2
                        factorPregY2 = herdParameter.PregRateDecreaseY2;
                    } else {
                        factorPregY2 = 1;
                    }
                    if (x < prob_preg * factorPregY2) {//Probability of being Pregnant
                        // Pregnant!
                        herd[i][4] = 2; // Change Lact Status to Pregnant = 2
                        herd[i][7] = 0; // First day of pregnancy
                        Num_pregnant = Num_pregnant + 1;
                        herd[i][8] = 0; // reset number of unsuccesful AI
                        herd[i][18] = 1; // Pregnant Today
                        herd[i][23] = herd[i][23] + InsemCost; // Add cost or AI
                    } else {
                        // if not pregnant
                        herd[i][8] = herd[i][8] + 1; // Number of unsuccesful AI.
                        non_pregnant = non_pregnant + 1;
                        herd[i][23] = herd[i][23] + InsemCost; // Add cost or AI
                        //                             if herd[i][9)== AIattempts
                        //                             herd[i][20)= herd[i][1)+21; // Day to cull the cow, after finding out it is not pregnant

                    }
                } // end of insemination attempts
            } else if (herd[i][30] == 1) { // cull cow after lactation
                if (herd[i][5] > 0) {// If parity > 0
                    herd[i][9] = herd[i][9] + 1; // Increase DIM by 1
                }
            }
        } // end conditional statements on pregnant or calving

        ////// If Susceptible Calves ////// MAP
        if (herd[i][3] == 5) { // Susceptible calf (SC)
            //NumSC= NumSC + 1; // update number of SC

            if (herd[i][2] >= 60) { // if 60 days old or more, become Heifer
                Num_Heifers = Num_Heifers + 1; // Increase the number of New heifers
                herd[i][3] = 7; // Change to Susceptible Heifers, 7.

            } else { // if younger than 60 days old
                // estimate horizontal infection, calf to calf

                double x = Math.random(); // get random number
                if (x < C_Inf) { //calf_infec_chance  Horiz Trans Calf-calf. calf_infect_prob // horizontal infection among calves
                    Num_InfCalves = Num_InfCalves + 1;
                    herd[i][3] = 6; // Change infection status to Infected Calf
                } // end of horizontal infection among calves

            } // Calf age conditionals
            ////// If Infected Calves //////
        } else if (herd[i][3] == 6) {// Infected Calf (IC)
            //NumIC= NumIC + 1; // update number of IC
            if (herd[i][2] >= 60) { // if 60 days or older, become Heifer
                Num_Heifers = Num_Heifers + 1; // Increase the number of New heifers
                herd[i][3] = 8; // Change to Infected Heifers, 8.
            }
            ////// If Susceptible Heifers //////
        } else if (herd[i][3] == 7) { // If Susceptible Heifer (SH)
            // NunSH= NumSH + 1; // Update number of SH

            if (herd[i][2] >= 440 && herd[i][4] != 2) {
                herd[i][4] = 1; // Ready to be inseminated
                // Genetic Milk Value [1-5]
                herd[i][29] = 3; // 3= no change from genetic differences
            }
            double x = Math.random(); // Random Number
            if (x < herdParameter.H_Inf) {//heif_infec_chance; //MM. H_Inf && herd(i,3)< 720; //PAPER. Horiz trans heif-heif. heif_infec_chance && herd(i,3)< 720
                Inf_Heifer = Inf_Heifer + 1; // Count number of infected Heifers
                herd[i][3] = 8; // Change Status to Infected Heifer
            } // end Horizontal Infection among Heifers
            // Inseminate if age== 440
            if (herd[i][4] == 1 && (herd[i][2] == 440 || herd[i][2] == 461 || herd[i][2] == 482 ||
                    herd[i][2] == 503 || herd[i][2] == 524 || herd[i][2] == 545
                    || herd[i][2] == 566 || herd[i][2] == 587 || herd[i][2] == 608 || herd[i][2] == 629 || herd[i][2] == 750 || herd[i][2] == 771)) {

                herd[i][5] = 0; // Parity to 0, becomes 1 AFTER it calves or becomes pregnant.
                int par_status = 0;
                int days_in_milk = 0; // DIM
                //prob_preg= prob_insi_suc70(par_status,prob_pregInput); // Function to estimate AI Success Prob.
                double prob_preg = herdHelperFunc.prob_insi_suc70(par_status, herdParameter.prob_pregInput, herdParameter.annualProb, herdParameter.AIattempts, herdParameter.MaxParity);
                x = Math.random();
                herd[i][17] = herd[i][0]; // Time of AI
                if (x <= prob_preg) {// per day probability of being PREGNANT for 1000 animals, the parameter was found by trial and error process for different parity, as there is insufficient data
                    // Pregnant!
                    herd[i][4] = 2; // Change status to Pregnant
                    herd[i][5] = 0; // Parity 0, become 1 after it calves
                    herd[i][7] = 0; // First day of pregnancy
                    Num_pregnant = Num_pregnant + 1; // count numner of pregnancies
                    herd[i][8] = 0; // Reset counter of unsuccesful AI
                    herd[i][18] = 1; // Pregnant Today
                    herd[i][23] = herd[i][23] + InsemCost; // Add cost or AI
                } else { // If not pregnant
                    herd[i][7] = 0; // First day of pregnancy
                    herd[i][8] = herd[i][8] + 1; // Number of unsuccesful AI.
                    non_pregnant = non_pregnant + 1;
                    herd[i][23] = herd[i][23] + InsemCost; // Add cost or AI
                } // end of pregnancy function
            }
            //end of heifer insemination conditinal
            ////// If Infected Heifers //////
        } else if (herd[i][3] == 8) {// If Infected Heifer
            //NumIH= NumIH + 1; // Number of Infected Heifers

            if (herd[i][2] >= 440 && herd[i][4] != 2) {
                herd[i][4] = 1; // Ready to be inseminated
                // Genetic Milk Value [1-5]
                herd[i][29] = 3;

            }

            // Inseminate if age== 440 or any 21 days after that
            if (herd[i][4] == 1 && (herd[i][2] == 440 || herd[i][2] == 461 || herd[i][2] == 482 ||
                    herd[i][2] == 503 || herd[i][2] == 524 || herd[i][2] == 545
                    || herd[i][2] == 566 || herd[i][2] == 587 || herd[i][2] == 608 || herd[i][2] == 629 || herd[i][2] == 650 || herd[i][2] == 671)) {

                herd[i][5] = 0; // Parity to 0, becomes 1 AFTER it calves or becomes pregnant.
                double par_status = 0;
                double days_in_milk = 0; // DIM
                double prob_preg = herdHelperFunc.prob_insi_suc70((int) par_status, herdParameter.prob_pregInput, 0, 0, 0); // Function to estimate AI Success Prob.????????????????????????????????????????????!!!!!!!!!!!!!!!!!!
                double x = Math.random();
                herd[i][17] = herd[i][0]; // Time of AI
                if (x <= prob_preg) { // per day probability of being PREGNANT for 1000 animals, the parameter was found by trial and error process for different parity, as there is insufficient data
                    // Pregnant!
                    herd[i][4] = 2; // Change status to Pregnant
                    herd[i][5] = 0; //Parity 0, become 1 after it calves.
                    herd[i][7] = 0; // First day of pregnancy
                    Num_pregnant = Num_pregnant + 1; // count number of pregnancies
                    herd[i][8] = 0; // reset counter of unsuccesful AI
                    herd[i][18] = 1; // Pregnant in
                    herd[i][23] = herd[i][23] + InsemCost; // Add cost or AI
                } else { // If not pregnant
                    herd[i][7] = 0; // First day of pregnancy
                    herd[i][8] = herd[i][8] + 1; // Number of unsuccesful AI.
                    non_pregnant = non_pregnant + 1;
                    herd[i][23] = herd[i][23] + InsemCost; // Add cost or AI
                    //                         if herd[i][8]== AIattempts
                    //                             herd(i,20)= herd[i][0]+21; // Day to cull the cow, after finding out it is not pregnant
                    //                         end
                } // end of pregnancy function

            } // end of heifer insemination conditinal

        } // end Calves and Heifers Conditionals
        if (herd[i][5] == herdParameter.MaxParity && (herd[i][4] == 1 || herd[i][4] == 3)) { //herd(i,8)>=220 // if number of Parity > Max
            herd[i][14] = 5; // OverParity, Cull the animal in the next iteration
            // overPar= overPar+1; // Number of culled open cows
        }
        if (herd[i][4] == 2 && (herd[i][0] == herd[i][17] + 42 || herd[i][0] == herd[i][17] + 60 || herd[i][0] == herd[i][17] + 220)) {// If 42, 60 or 220 days after AI AND Pregnant
            herd[i][23] = herd[i][23] + PregnancyDiagnosis; // Add cost of pregnancy diagnosis
        }
        ////// Cull if Open Cows //////
        // From MAP Code
        if (herd[i][8] >= herdParameter.AIattempts && herd[i][19] == 0) {// If over AI attempts and not yet set to be culled
            herd[i][19] = herd[i][0] + 21; // Cull 21 days later, after finding out it is not pregnant.
        }
        if (herd[i][0] == herd[i][19]) { // cull after it is diagnosed not pregnant
            herd[i][14] = 4; // Open Cow, Cull the animal in the next iteration
            //op= op+1; // Number of culled open cows
        }
        double MAPTime, par_status, days_in_milk, Season, LS, MAPStatus, MilkGenetics, milk;
        if (herd[i][3] == 1 || herd[i][3] == 2 || herd[i][3] == 3 || herd[i][3] == 4) { // Estimate Milk Production for Cows
            if (herd[i][9] > 0) {// if DIM > 0
                if (herd[i][3] != 1 && herd[i][10] > maxMAPDays) {// Truncate the value of Days in Current Infection Status to 150,300, 600, otherwise Milk Yield is < 0, or too low.
                    MAPTime = ceil(maxMAPDays / 30); // number of months in current infection status, otherwise Y2 prod < 0
                } else if (herd[i][3] != 1 && herd[i][10] <= maxMAPDays) { // MAP Days 300, gives a max loss for Y2 of 31///year, and max of 37///day .
                    MAPTime = ceil(herd[i][10] / 30); // number of months in current infection status
                } else {
                    MAPTime = 0; // for Susceptible cows
                }
                par_status = herd[i][7]; // Parity number
                days_in_milk = herd[i][9]; // DIM

                Season = herdHelperFunc.season(t); // estimate season
                LS = 3.5; //2.48; // The average of Becky's Data is 2.48, Log somatic cell score, but use 8 to have lower (more realistic) milk yields.

                // Make Latent Cows Susceptible for Milk Production
                if (herdParameter.MilkProdSame == 1 && herd[i][3] == 2) { // Change it to S if L
                    MAPStatus = 1;
                } else {
                    MAPStatus = herd[i][3]; //
                }

                MilkGenetics = herd[i][29]; // Milk Production Genetics
                // Milk Genetic is the change in milk production due to
                // genetics. This is a number [1-5]. 3= no change, 1= -5, 2=
                // -2.5, 4= +2.5, 5= +5. This is added to the intercept.
                // Milk Genetics is assigned at birth.

                milk = herdHelperFunc.Milk_ProductionMAPHS((int) par_status, (int) days_in_milk, (int) Season, LS, MAPStatus, MAPTime, (int) MilkGenetics);
                herd[i][11] = milk;
            } else {
                herd[i][11] = 0;
            }
        }
        if (herd[i][9] > 0) {
            herd[i][13] = herd[i][11] * herdParameter.MilkPrice; // Milk Revenue

        } else if (herd[i][9] == 0 && (herd[i][3] == 1 || herd[i][3] == 2 ||
                herd[i][3] == 3 || herd[i][3] == 4)) { // Cows Not Producing Milk
            //herd[i][12]= herd[i][12]+ FeedCostDryCow; // Maintenance Feed Cost
            herd[i][13] = 0; // No Milk Revenue
        }
        ////////////////// Estimate Body Weight (BW) and Dry Matter Intake (DMI)
        ////// Body Weight in Kg

        par_status = herd[i][5]; // Parity number
        double Age = herd[i][2]; // Age in days
        double Preg = herd[i][7]; // Pregnant Days
        double dim = herd[i][9]; // Days in Milk
        milk = herd[i][11]; // Milk production

        double BWt = herdHelperFunc.BW((int) dim, milk, (int) Preg, (int) Age, (int) par_status, (int) MatureWeight);  // This accounts for pregnancy/
        //[BWtnoP]= BW(dim,milk,0,Age,par_status,MatureWeight);  // This accounts for pregnancy/

        herd[i][20] = BWt; // This is the weight without the fetus weight, to estimate carcass value

        ////// Estimate DMI
        int WOL = (int) Math.ceil(dim / 7); // Weeks on Milk. Start in week 1.

        double dmi = herdHelperFunc.Dmi_Cow(BWt, milk, WOL, (int) par_status);
        herd[i][21] = dmi; // save dmi in herd matrix


        ////// Estimate Daily Feed Cost

        herd[i][22] = herd[i][21] * herdParameter.DMICost; // Daily Feed Costs in Kg/day
        ////// Total Var+Fixed Costs for Cows and Heifers
        double NonFeedCosts = herdHelperFunc.VarsFixedCosts(Age, FixedVarsCostDay, CostCalf, par_status);

        herd[i][23] = NonFeedCosts; // Sum of Non feed variable costs plus fixed costs.

        ////////// Factor to decrease metabolic efficiency in Y2.
        // This is reflected in higher total feed costs (pregnancy, maintenance and
        // production)
        if (herd[i][3] == 4) {
            if (herd[i][9] > 0) {// If High Shedders AND Producing Milk, add extra feed cost
                herd[i][22] = herd[i][22] * HighShedFeedCostFactorMilkProd; // Factor that increases feed intake for same milk production (decrease in metabolic efficiency)
            } else {
                herd[i][22] = herd[i][22] * HighShedFeedCostFactorNoMilk; // Factor that increases feed intake while dry (decrease in metabolic efficiency)
            }
        }
        // NEW MANAGEMENT/CONTROL STRATEGY COSTS
        herd[i][12]= 0;  // Make sure costs are not accumulating, set to zero before adding new ones
        if (herdParameter.ControlCullTest== 1 && t>= herdParameter.WarmUp+ herdParameter.StabilityTime) {

            int timeFirstTest = 180; // first Elisa or FC test after warm up period is over.
            int repeatYear; // Remainder of dividing t by WarmUp+timeFirstTest, use this number to calculate annual intervals of testing
            repeatYear = (herdParameter.WarmUp + herdParameter.StabilityTime + timeFirstTest) % 365;
            int modtoday = t % 365; // Remainder of dividing t by WarmUp+timeFirstTest, use this number to calculate annual intervals of testing
            if (herd[i][5] >= herdParameter.ParityTest && herd[i][27] == 0 && herdParameter.elisaTest == 1 && (modtoday == repeatYear)) {// Run Elisa Test after 180 days of infection
                // If Susceptible or Latent (False Positive)
                herd[i][27] = herd[i][27] + 1; // Number of ELISA tests
                herd[i][12] = herd[i][12] + herdParameter.costElisa; // Cost of Elisa Test
                if (herd[i][27] == 1) { // If first test, estimat the probablity of FP, FN, etc
                    if (herd[i][3] == 1 || herd[i][3] == 2) {
                        double x = Math.random(); // random draw for False Positive Y1
                        double x1 = Math.random(); // random draw for False Positive Y2
                        if (x <= (1 - herdParameter.FrequencyTest * herdParameter.TestSpecificityElisa)) { //  False Positive to Y1, follow Becky (2017)
                            herd[i][26] = 3; // Test Result as Y1, POSITIVE
                        } else if (x1 <= (1 - herdParameter.FrequencyTest * herdParameter.TestSpecificityElisa)) { //  False Positive to Y2, follow Becky (2017)
                            herd[i][26] = 4; // Test Result as Y2, POSITIVE
                        } else { // Not False Positive
                            herd[i][26] = herd[i][3]; // Test Result is True Negative, stay Susceptible or Latent
                        }
                        // If Low Shedder (False Negative)
                    } else if (herd[i][3] == 3) {
                        double x = Math.random();
                        if (x <= (1 - herdParameter.FrequencyTest * herdParameter.TestSensitivityElisaY1)) {// False Negative Prob
                            herd[i][26] = 2; // Tested Negative (Latent)
                        } else { // True Positive
                            herd[i][26] = herd[i][3]; // True Positive, stay Y1
                        }
                        // If High Shedder (False Negative)
                    } else if (herd[i][3] == 4) { // High Shedder
                        double x = Math.random();
                        if (x <= (1 - herdParameter.FrequencyTest * herdParameter.TestSensitivityElisaY2)) {// False Negative Prob
                            herd[i][26] = 2; // Tested False Negative (Latent)
                        } else { // True Positive
                            herd[i][26] = herd[i][3]; // True Positive Y2
                        }
                    }
                }
            }
            // FC Test, done after ELISA POSITIVE. Serial test
            if (herd[i][27] == 1 && (herd[i][26] == 3 || herd[i][26] == 4)) { // If Elisa Test done AND Y1 or Y2 Positive
                // If Susceptible or Latent (No False Positive with FC Test)

                herd[i][12] = herd[i][12] + herdParameter.costFC; // Cost of FC Test
                herd[i][26] = 0; // Reset test results to 0. Here we save FC Tests results now.
                //if herd[i][27]== 2 // IF ELISA POS. stimate prob. of FP, FN, etc..
                if (herd[i][5] == 1 || herd[i][5] == 2) {
                    double x = Math.random(); // random num (0,1)
                    double x1 = Math.random(); // Independent random num from x
                    if (x <= (1 - herdParameter.FrequencyTest * herdParameter.TestSpecificityFC)) {//  False Positive to Y1, follow Becky (2017)
                        herd[i][26] = 3; // Test Result as Y1
                    } else if (x1 <= (1 - herdParameter.FrequencyTest * herdParameter.TestSpecificityFC)) { //  False Positive to Y2, follow Becky (2017)
                        herd[i][26] = 4; // Test Result as Y2
                    } else { // Not False Positive
                        herd[i][26] = herd[i][5]; // Test Result is True Negative, stay Susceptible or Latent
                    }

                    // If Low Shedder (False Negative)
                } else if (herd[i][5] == 3) {
                    double x = Math.random(); // random num (0,1)
                    if (x <= (1 - herdParameter.FrequencyTest * herdParameter.TestSensitivityFCY1)) { // False Negative Prob
                        herd[i][26] = 2; // Tested Negative (Latent)
                    } else { // True Positive
                        herd[i][26] = herd[i][5]; // True Positive, stay Y1
                    }
                    // If High Shedder (False Negative)
                } else if (herd[i][5] == 4) {// High Shedder
                    double x = Math.random(); // random num (0,1)
                    if (x <= (1 - herdParameter.FrequencyTest * herdParameter.TestSensitivityFCY2)) {// False Negative Prob
                        herd[i][26] = 2; // Tested False Negative (Latent)
                    } else { // True Positive
                        herd[i][26] = herd[i][5]; // True Positive Y2
                    }

                }


                if (herd[i][26] == 4 || herd[i][26] == 3) { // if tested Positive Y1 or Y2, Cull
                    //herd(i,29)= t+TimetoCull; // Future CUlling Date, 90 days from now, or 45

                    if (herdParameter.ControlCullCalvesTest == 1) {// If culling the latest calf as well

                        for(int m = 0; m < herdHelperFunc.getSize(); m++ ){
                            if(herd[m][1]==herd[5][32]){
                                herd[m][28] = t+ herdParameter.TimetoCull;
                            }
                        }

                        if (herdParameter.LowMilkCullNow == 1) {// If 1, cull immediately Low Producting Cows due to MAP
                            herd[i][28] = t + herdParameter.TimetoCull; // Future CUlling Date, 90 days from now, or 45


                        } else { // Otherwise, cull at the end of lactation, or when Dry day=1
                            herd[i][30] = 1; // Indicator that cow will be culled at day 1 of dry period (pregnancy days==220)
                            herd[i][31] = t + herdParameter.TimetoCull; // FC Results Date, 90 days from now, or 45

                        }


                    }
                }
                herd[i][30] = 0;

                if (herd[i][31] == t && herd[i][28] == 0 && herd[i][30] == 1 && herd[i][9] > 0) {// FC Test received and Lactating
                    if (herd[i][7] > 0) { // If cow is in milk and pregnant AND indicate that will be culled due to low prod
                        herd[i][28] = t + (220 - herd[i][7]); // culling date is at start of dry period (preg days is 220)
                    } else if (herd[i][7] == 0) { // If not pregnant and in milk
                        herd[i][28] = t + (305 - herd[i][9]); // Cull after 305 days in lactation if not pregnant
                    }
                } else if (herd[i][31] == t && herd[i][28] == 0 && herd[i][30] == 1 && herd[i][9] == 0) {// FC Test received but NOT LActating
                    herd[i][28] = t; // Cull now if not milking
                }
                if (herd[i][28] == t) { // If Culling Date arrives
                    herd[i][14] = 6; // Cull
                }
            }

            if (herdParameter.ControlCullLowMilk == 1 && t >= herdParameter.WarmUp + herdParameter.StabilityTime) {
                if (herd[i][4] == 4 && herd[i][10] >= herdParameter.LowMilkDays && herd[i][9] >= herdParameter.LowMilkDays) {
                    if (herdParameter.ControlCullCalvesTest == 1) {// If culling the latest calf as well
                        for(int k = 0; k < herdHelperFunc.getSize(); k++ ){
                            if(herd[k][1]==herd[i][32]){// Location of Calve's ID
                                herd[k][14] = 6;// Cull Calf Immediately, regardless of culling date of cow
                            }
                        }
                    }
                    if (herdParameter.LowMilkCullNow == 1) { // If 1, cull immediately Low Producting Cows due to MAP
                        herd[i][14] = 6; // Mark IdElim cows as cull for over limit

                    } else { // Otherwise, cull at the end of lactation, or when Dry day=1
                        herd[i][30] = 1; // Indicator that cow will be culled at day 1 of dry period (pregnancy days==220)
                    }
                }
                if (herd[i][28] == 0 && herd[i][30] == 1 && herd[i][7] > 0) {// If cow is pregnant AND indicate that will be culled due to low prod
                    herd[i][28] = t + (220 - herd[i][7]); // culling date is at start of dry period (preg days is 220)
                } else if (herd[i][28] == 0 && herd[i][30] == 1 && herd[i][7] == 0) { // If Not Pregnant
                    herd[i][28] = t + (305 - herd[i][9]); // Cull after 305 days in s if not pregnant
                }
                if (herd[i][28] == t) {// If Culling Date arrives
                    herd[i][14] = 6; // Cull
                }

            }
        }
        /**
         * Important change: create a new column that represent the sum of the milk production
         */
        herd[i][43]+= milk;
        // (int)herd[i][5] is parity number and herd[i][43] is milk sum
        int Repro = herd[i][7] > 0 ? 1 : 0;
        herd[i][44] = herdHelperFunc.ME305((int)dim, (int)herd[i][5], herd[i][43]) + Beta * Repro;
    }
}
