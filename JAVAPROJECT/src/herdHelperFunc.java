import java.io.File;
import java.io.FileNotFoundException;

import java.util.*;
import java.lang.String;

import java.util.Scanner;

public class herdHelperFunc {

    //Help function to decide the chance of Infection
    //Function to simulate infections from adults, given probability S_inf_chance
    //Suceptibles are infected by three routes fecal-oral,Y1 and Y2
    //Fecal-oral transmission only for susceptible adults
    public static int infection_chance(int S_Inf_AA){
        double x = Math.random();
        int S_L;
        if(x < S_Inf_AA)
            S_L = 2;
        else
            S_L = 1;
        return S_L;
    }




/*
    Inputs of the function:
    Parity (Lactation Number), divided into first and the rest.
    Somatic Cell Linear Score (LS, Log of the SCC)
    Days in Milking (DIM)
    Season: Winter (Jan-Mar), Spring (Apr-Jun), Summer (Jul-Sep), Fall
    (Oct-Dec)
    MAP Path (constant): MAP Infection path based on tests.
    Negative: All test are neg. (Susceptible)
    Low: at least one positive test, either Fecal Culture with <50 cfu/tube,
    ELISA, or Postmortem tissue culture.
    High: at least one positive FC or tissue culture with >50cfu/tube.

    MAP Status (time dependent): 4 categories.
    Negative, Latent, Low Shedding, and High Shedding.

    MAP Time (Months Spent at a Particular, current, Status Level): 0 if MAP
    Path is Negative; othewise it is the num of months in the non-neg status.
    Months in Latent, Y1, or Y2.

    Herd. NY is the baseline.
    Two Models are considered, with and without MAP Path.

    The Full Model is:
    Milk= B0+ B1*Parity+ B2*LS+ B3*Season+ B4Herd+ B5DIM+ B6*e^(-0.1*DIM)+
    D1*DIM*Parity+ D2*e^(-0.1*DIM)*Parity+ B7*MAPPath+
    B8*MAPStatus+ B9*MapTime+ D3*MapTime*MAPStatus

    Esimated Parameters are: B0-9, D1-2

    LS is estimated or set to an average. In Becky's data, the average is
    2.48.
    Season is the result of a functionwt
    Av. Milk Prod In Becky's data is 70.4 Lbs/ day


    This function does not include MAP Path



    The parameter B10LS is published in the paper as 0.87, which would make
    Y2 production > Susceptible for large MAPTime.
    That is why it is changed to 0.80, so that it is always less than S but
    close in value.
*/

    public static double Milk_ProductionMAPHS(int par_status, int days_in_milk, int Season, double LS, double MAPStatus, double MAPTime, int MilkGenetics){

        double B0= 42.44;  //    Intercept
        double B1= 9.73;   //   >1 Lactation
        double B2= -0.98;  //    Linear Score of Somatic Cell (Log of SCC)
        double B3Sp= 2.59; //   Effect of Spring Season with respect to Fall
        double B3Su= 1.45; //   Summer Effect wrt Fall
        double B3Wi= 1.24; //   Winter Effect wrt Fall
        double B5= -0.02;  //   Days in Milking (DIM)
        double B6= -27.82; //    exp of DIM, exp(-0.1*DIM)
        double D1= -0.03;  //   DIM * Parity >1
        double D2= 0.8;    //   exp(-0.1*DIM) * Parity>1
        double B7La= 1.5;  //   Latent (Base is Susceptible, negative)
        double B7LS= -1.37; //   Low Shedding
        double B7HS= -3.96; //   High Shedding
        double B8= -0.79;  //   MAP time (in months) in the current non-neg status
        double B9La= 0.71;//    MAP Time * MAP Status (Latent)
        double B9LS= 0.87;//    MAP Time * MAP Status (Low Shedders), Base is High Shedders

        int Spring=0, Summer=0, Winter=0;
        int Latent=0, LowShed = 0, HighShed=0;
        int par_statusMod=0;




        double ProdAdj= -5.6;//   Production adjustment in relation to NYS average  production
        //         This adjustment brings the average production per year to
        //         23,000 lb. per cow.


        //Change the intercept according to Genetic Potential. if 3, leave as it is.
        if	(MilkGenetics == 5)
            B0 += 5;
        else if (MilkGenetics == 4)
            B0+=2.5;
        else if (MilkGenetics== 2)
            B0= B0-2.5;
        else if (MilkGenetics== 1)
            B0= B0-5;

        //Spring
        if (Season==1)
            Spring = 1;
        else if (Season==2)
            Summer = 1;
            //Fall, this is the baseline
        else if(Season == 4)
            Winter = 1;


        //The difference between this model and ProductionMAP is the number
        //assigned to each MAP Status, Here it is 1-4; in the other model (1,3,4,5)

        //if (MAPStatus == 1) //1 = Susceptible
        //Do nothing
        if (MAPStatus == 2)
            Latent = 1;
        else if(MAPStatus == 3)
            LowShed = 0;
        else if(MAPStatus == 4)
            HighShed = 1;
        // IF none of above happen, keep as Susceptible


        //MAPTime, Number of months spent at a particular status level
        if (MAPStatus == 1)
            MAPTime = 0; //MAP Time is 0 for Susceptible COws



        if (par_status > 1)
            par_statusMod = 1;
        else
            par_statusMod = 0;

        //decrease milk production by parities >7
        double milk= B0+ B1*par_statusMod+ B2*LS+ B3Sp*Spring+ B3Su*Summer+ B3Wi*Winter+
                B5*days_in_milk+ B6*Math.exp(-0.1*days_in_milk)+
                D1*days_in_milk*par_statusMod+ D2*par_statusMod*Math.exp(-0.1*days_in_milk)+
                B7La*Latent+ B7LS*LowShed+ B7HS*HighShed+ B8*MAPTime+
                B9La*MAPTime*Latent+ B9LS*MAPTime*LowShed + ProdAdj ;




        return milk;
    }




    public static double VarsFixedCosts (double Age, double FixedVarsCostDay, double CostCalf, double par_status) {
        double NonFeedCosts = 0;
        if (Age == 1) {
            NonFeedCosts = CostCalf; // Cost of newborn Calf

        }else if( Age>1 && Age <= 28) {
            NonFeedCosts = 5.0; // 6.09

        }else if (Age>28 && Age <= 439){
            NonFeedCosts = 0.85;  // 1.95;

        }else if (Age>439 && Age <= 699) {
            NonFeedCosts = 1.4;  // 1.57;

        }else if(Age>699 && par_status == 0) {
            NonFeedCosts = 1.4;  // 2.12;

        }else if(Age>720 && par_status > 0){
            NonFeedCosts = FixedVarsCostDay;

        }
        return NonFeedCosts;
    }




/*
    Function to estimate seasons accordin to days in
    simulation. Assuming simulation begins in Jan, 1; and no leap years.

    input is number of days in simulation, t. Where t > 0.
    output is season, as defined in Smith et al. (2016)
    Seasons are coded as:
    Spring= 1
    Summer= 2
    Autumn= 3
    Winter= 4

    Spring (1): April to June, (91-181) 91 day
    Summer (2): July to September, (182-273), 92 days
    Autumn (3): Octuber to December (274-365), 92 days.
    Winter (4): January to March, (1-90) 90 days
*/

    public static int season (int t){
        int daysYear = 0;
        int season = 0;

        if(t%365==0)
            daysYear = 365;
        else
            daysYear = (t%365);

        if(daysYear >=91 && daysYear<= 181)
            season= 1;//Spring
        else if (daysYear>= 182 && daysYear<= 273)
            season = 2;//Summer
        else if (daysYear>= 274 && daysYear<= 365)
            season = 3;//Autumn
        else if (daysYear>= 1 && daysYear<= 90)
            season = 4;

        return season;
    }
/*
    Natural culling function, open cows prob is deducted from
    the paritywise culling prob


    This function needs to be revised.
    prob(InsemTrial)= 1-exp(-0.7/9)= 0.0748; since max 9 Insem. Attempts
    prob(InsemTrial)= 1-exp(-0.7/8)= 0.0838; since max 8 Insem. Attempts
    prob(InsemTrial)= 1-exp(-0.7/7)= 0.0952; since max 7 Insem. Attempts
    prob(InsemTrial)= 1-exp(-0.7/6)= 0.1101; since max 6 Insem. Attempts
    In other models (e.g Karul's), he sets the pregnancy prob to about 20
    per AI event.

    Pregnancy rate is the product of heat detection rate and conception
    rate.
    Heat Detection ranges from 40-60  ; Conception rate from 30-60  ;
    Pregnancy rate  from 18-36  . Mich S U

    Change the pregnancy rate to 20  , similar to Karun's.
*/
    public static double prob_insi_suc70(int par_status, double prob_pregInput, double annualProb, int AIattempts, int MaxParity){
/*
    set pregnancy probability to 70  , or per attempt (8/year)= 13.97
    THE FUNCTION BELOW has a constant pregprob.
    if par_status>=0 && par_status<=9
    prob_preg= prob_pregInput;    Set same preg prob.
    else
    prob_preg= 0;    if parity >9 cull cow
    end

    ALL PARITIES HAVE SAME PROBABILITY OF PREGNANCY: 13.97  , SUCH THAT THE
    TOTAL YEARLY PROBABILITY IS 70   (IN 8 ATTEMPTS), SAME FOR HEIFERS.
*/
        double prob_preg = 0;
        if(par_status == 0){
            prob_preg= prob_pregInput; //   1    MM, all heifers get pregnantd
        }
        else if (par_status < MaxParity) {
            prob_preg= 1 - Math.exp(Math.log(1-annualProb) / AIattempts);

        }
        else if (par_status >= MaxParity){
            prob_preg = 0;
        }
		else{
            prob_preg = 0;
        }

        return  prob_preg;
    }


/*

    Natural culling function, open cows prob is deducted from
    the paritywise culling prob
    These are conditional probabilities of natural culling given ONLY the number of lactation.

    Maximum parity after which the cow is culled can be changed. Currently
    set at 8.
    For the Natural Cull Prob. since in the paper they include both nat cull
    and sales, we used the nat cull prob for Heifers x 2, so Cows are twice as likely to die than Heifers.
    (0.0000278 vs 0.0000556) and adjusted the prob. so that older cows are
    more likely to be involuntarily culled than younger cows.
*/


    public static double  nat_cull_prob(int par_status, double factorcull){
        double factorcullHighPar = 1.1 *factorcull;
        double death_prob = 0;
        if(par_status==0){// parity number. Lactation number.
            death_prob = 0;
        }
        else if(par_status == 1){// 1st lactation 4// annual prob= 0.0001096
            death_prob = 0.0001096*factorcull;//0.000167; // MM
            //death_prob= 0.0000356;
        }
        else if (par_status == 2){
            death_prob= 0.000132*factorcull;
            //death_prob= 0.0000456;
        }
        else if (par_status == 3){
            death_prob= 0.0001918*factorcull;
            //     death_prob= 0.0000556;
        }
        else if (par_status == 4){
            death_prob= 0.0002192*factorcull;
            //     death_prob= 0.0000556;
        }
        else if (par_status == 5){
            death_prob= 0.0002466*factorcullHighPar;
            //     death_prob= 0.0000656;
        }
        else if (par_status == 6){
            death_prob= 0.0002739*factorcullHighPar; //0.000556;
            //     death_prob= 0.0000756;0.0002739
        }
        else if (par_status == 7){
            death_prob= 0.0003014*factorcullHighPar;
            //   death_prob= 0.0000856;
        }
        else if (par_status == 8){
            death_prob= 0.00038365*factorcullHighPar; //0.000833;
            //     death_prob= 0.0000956;
        }
        else {
            death_prob= 0.000411*factorcullHighPar; //0.000933; // MM = 1; 15//= 0.000411
            //  death_prob= 0.0000956;
        }
        return  death_prob;
    }



/*
    Function that estimates Body Weight (BW) for calves, heifers, and cows.
    The animal weight is the sum of three components: function of age, lactation, and pregnancy.
    Formula from Korver, and Van Arendonk (1985), "A Function for Live-Weight...
    Change Between two Calving in Dairy Cattle", Animal Productio 1995.

    Estimates DMI from the parameters:
    BWt: Body Weight total (kg);
    Bwa: Body Weight as a function of age
    Bwl: Body Weight as a function of lactation
    Bwp: Body Weight as a function of pregnancy
    Milk: milk production
    Parity: Parity Number
    Age: age in day
    MatureWeight: Expected weight of a full growns cow (pregnant)
    Preg: days pregnant
    P3 and P4, max Kg gained in lactation, and days to reach it,
    respectively.
    P2, P5: Shape parameters in the function of pregnancy
*/


    public static double BW(int dim, double milk, int Preg, int Age, int par_status, int MatureWeight){

        int BirthWeight =42;
        double k= 0.0039; //Growth Rate Parameter (Karun's Model)
        double P2= 0.015; // Shape Parameter
        int P5= 50; // Days in which the fetus does not has a significant weight
        //BWa= 0; BWl= 0; BWp= 0; // Initialize variables
        int P3=0, P4=0;

        // Function of age
        ///Make sure the fuction is correct
        double ratio = (double)BirthWeight/(double)MatureWeight;
        double exp = Math.exp(-k*Age);
        double pow = Math.pow(ratio,(0.33333333));
        double BWa= ((double)MatureWeight)*Math.pow((1-  ( (1-pow) *  exp)),3);
        double BWl, BWp, BWt;

        if (milk>0 && par_status==1 ){// If producting Milk
            P3= 20; // kg gained
            P4= 65; // Days to reach it
            // Function of Lactation for Heifers (first calving)
            BWl= (P3*dim/P4)*Math.exp(1-dim/P4);
        }
        else if(milk>0 && par_status> 1){
            P3= 40; // kg gained
            P4= 70; // Days to reach it
            // Function of Lactation for Cows (>first calving)
            BWl= (P3*dim/P4)*Math.exp(1-dim/P4);
        }
        else{
            BWl = 0;
        }

        if(Preg - P5 > 0){
            BWp= Math.pow(P2,3)*Math.pow((Preg-P5),3);  // Increase in weight is > 50 Days Pregnant
        }
        else{
            BWp = 0;
        }
        BWt= BWa+BWl+BWp;

        return BWt;
    }
/*
    Function that estimates Dry Matter Intake (DMI) for Lactationg Cows
    Formula from NRC(2001), 4  Fat Corrected Milk formula from Gaines and
    Davidson (1923).
*/
    public static double Dmi_Cow(double BWt, double milk, int WOL, int par_status){
        double NE = 2.3;// Net Energy (Mcal/Kg)
        double Fat= milk*0.035; // Fat content in milk produced, assume 3.5// Fat.
        double FCM= milk*0.4+15*Fat; // Fat Corrected Milk (4// Fat)
        double dmi;
        if(par_status > 0){// For Cows (lactating and/or pregnant)
            if (milk==0 ){// If not lactating, set WOL to 0
                WOL= 0;
            }
            dmi= (0.372*FCM+0.0968*Math.pow(BWt,0.75))*(1-Math.exp(-0.192*(WOL+3.67))); // DMI for cows (kg/day)
        }else{// For Heifers
            dmi= Math.pow(BWt,0.75)*(0.2435*NE-0.0466*Math.pow(NE,2)-0.1128)/NE; // DMI for Heifers
        }
        return dmi;
    }

    public static double[] exit_L_Y1HS(double ExitRateLtoY1){
        ////Exit rate from E to Y1, if 0.667 per year, then per day,
        ////prob=1-exp(-0.667)=0.4868, daily prob in NOT=0.4868/365=0.0013
        // It should be prob=1-exp(-0.667/365)=0.0018
        int L_Y1 = 0, infection = 0;
        double x = Math.random();
        if(x < ExitRateLtoY1){
            L_Y1=3;
            infection=3;
        }else{
            L_Y1=2;
            infection=2;
        }
        double[] returnVal = new double[2];
        returnVal[0] = L_Y1;
        returnVal[1] = infection;

        return returnVal;
    }



    public static double[] exit_Y1_Y2HS(double ExitRateY1toY2){
/*
    Exit rate from Y1 to Y2, if 0.33 per year, then per day,
    prob=1-exp(-0.33)=0.2811/ year, now each day
    prob=0.2811/365=0.0003337, This is wrong...
    Daily prob is: prob= 1-exp(-rt), where r is rate, t is time.
    thus, prob(day)= 1-exp(-r/365)
    prob(day)= 1-exp(-0.33/365)= 0.0009037
    IN paper, daily prob is 0.00077
*/

        double x = Math.random();
        int Y1_Y2 = 0, infection = 0;
        if (x < ExitRateY1toY2){ // should be 0.0009037
            //exit_Y1_to_Y2= 1; // Progress to Y2

            Y1_Y2= 4; // infection status 4= Y2
            infection= 4;
        }else{
            //exit_Y1_to_Y2= 0; // Do not progress to Y2
            Y1_Y2= 3;  // infection status 3= Y1
            infection= 3;
        }
        double[] returnVal = new double[2];
        returnVal[0] = Y1_Y2;
        returnVal[1] = infection;
        return returnVal;
    }

    //Function to simulate infections from adults, given probability S_inf_chance
    //Suceptibles are infected by three routes fecal-oral,Y1 and Y2
    ////Fecal-oral transmission only for susceptible adults
    public static int infection_chance(double S_Inf_AA){
        double x = Math.random();
        int S_L;
        if(x < S_Inf_AA){
            S_L=2;
        }else{
            S_L=1;
        }
        return S_L;
    }
    public static double[] ini_ageHS(double age) {
        // age=randi([720, 2190],1,1);
        // age-what is the initial given age
        // par-which parity number is the animal in
        // par_age-what is the parity age from the theoretical lactation cycle
        // lac_status-which lactation status it is in. 0: before first pregnancy,
        //    1: Ready to be inseminated (go to pregnancy algorithm), 2: Pregnant or calving now,
        //    3: VWPt

        // Genetic Milk Value [1-5]
        int gen_milk = 3;
        int par=0, par_age=0, p;


        if (age - 720 == 0) {
            par = 1;
            par_age = 720;
        }
        // age parity
        if (age > 720) {
            p = (int)age - 720;
            par = (p / 340) + 1;
            if (par == 1) {
                par_age = 720;
            } else if (par == 2) {
                par_age = 1060;
            } else if (par == 3) {
                par_age = 1400;
            } else if (par == 4) {
                par_age = 1740;
            } else if (par == 5) {
                par_age = 2080;
            } else if (par == 6) {
                par_age = 2420;
            } else if (par == 7) {
                par_age = 2760;
            } else if (par == 8) {
                par_age = 3100;
            }

        }
        int vwp = 60;
        int dryperiod = 60;
        int dim = 0,lac_status=0;
        int age_dif = (int)age - par_age;
        int preg_days,days_left_calving,exp_calving_date;
        if (age_dif == 0) {
            lac_status = 2; //  calving now, status is 2= pregnant or calving now
            preg_days = 280;
            days_left_calving = 0;
            exp_calving_date = 720;
            dim = dim + age_dif;

        } else if (age_dif < vwp) {
            lac_status = 3; //it is in vwp
            preg_days = 0;
            days_left_calving = 0;
            exp_calving_date = 0;
            dim = dim + age_dif;
        } else if (age_dif == vwp) {
            lac_status = 1; //its already inseminated and it will go through the insemination algorithm
            preg_days = 0;
            days_left_calving = 0;
            exp_calving_date = 0;
            dim = dim + age_dif;

        } else if (age_dif > vwp && age_dif <= 340 - dryperiod) {
            lac_status = 2; // the cows are preg  nant
            preg_days = age_dif - vwp;
            days_left_calving = 280 - preg_days;
            exp_calving_date = par_age + vwp + 280;
            dim = dim + age_dif;

        }else if (age_dif>340 - dryperiod){
            lac_status = 2; // the cows are preg  nant
            preg_days = age_dif - vwp;
            days_left_calving = 280 - preg_days;
            exp_calving_date = par_age + vwp + 280;
            dim = 0;
        } else {
            preg_days = 0;
            days_left_calving = 0;
            exp_calving_date = 0;
            dim = 0;
        }
        double[] value = new double[]{age, par, par_age,lac_status,preg_days,days_left_calving,exp_calving_date,dim,gen_milk};
        return value;
    }


    private static int size_of_herd = 0;
    private static double herd[][];
    public static double [][] DataImport(){
        String fileName = "herd_13shedExcel.csv";
        File file = new File(fileName);
        double[][] value = new double[3000][45];
        try{
            Scanner inputStream = new Scanner(file);
            int x = 0;
            while(inputStream.hasNext()){
                String data = inputStream.next();
                String Adata[] = data.split( ",");
                for(int y = 0; y < Adata.length; y++){
                    value[x][y] = Double.parseDouble(Adata[y]);
                }
                x++;

            }

        }catch (FileNotFoundException e){
            System.out.print("Do not find the address");
        }
        size_of_herd = 1956;
        herd = value;
        return value;
    }
    // @return the size of the current herd
    public static int getSize(){
        return size_of_herd;
    }

    // Push the cow into the herd in the herd loop
    public static void push_cow(double t,double id,double age,double infect_calf, double lac_status,
                               double  par, double par_age, double preg_days,double dim, double momID){
        herd[getSize()][0] = t;
        herd[getSize()][1] = id;
        herd[getSize()][2] = age;
        herd[getSize()][3] = infect_calf;
        herd[getSize()][4] = lac_status;
        herd[getSize()][5] = par;
        herd[getSize()][6] =par_age;
        herd[getSize()][7] =preg_days;
        herd[getSize()][8] =0;
        herd[getSize()][9] =dim;
        herd[getSize()][40]= momID;//39 or 40 or 41???
        size_of_herd++;
    }
    // Sort IdAgeC by requirement

    public static double[][] get_sorted_herd(double[][] input, final int col1, final int col2, final int col3, final boolean mode) {
        // Using built-in sort function Arrays.sort
        Arrays.sort(input, new Comparator<double[]>() {

            @Override
            // Compare values according to columns
            public int compare(final double[] entry1,
                               final double[] entry2) {

                // To sort in descending order revert
                // the '>' Operator
                if (mode==false) {
                    Double itemIdOne = entry1[col1];
                    Double itemIdTwo = entry2[col1];
                    return itemIdTwo.compareTo(itemIdOne);
                }else{
                    Double itemIdOne = entry1[col1];
                    Double itemIdTwo = entry2[col1];
                    if(entry1[col1]!=entry2[col1]){
                        return itemIdTwo.compareTo(itemIdOne);
                    }
                    else if(entry1[col2]!=entry2[col2]){
                        itemIdOne = entry1[col2];
                        itemIdTwo = entry2[col2];
                        return itemIdTwo.compareTo(itemIdOne);
                    }
                    else{
                        itemIdOne = entry1[col3];
                        itemIdTwo = entry2[col3];
                        return itemIdTwo.compareTo(itemIdOne);
                    }
                }

            }
        });  // End of function call sort().
        return input;
    }
    /*

    Create a matrix of calves by their ages

     */
    public static double [][] select_calves_by_age(){
        double [][] value = new double[getSize()][3];
        int counter = 0;
        for(int i = 0; i < getSize(); i++){
            if(herd[i][3]==5||herd[i][3]==6){
                value[counter][0] = herd[i][1];
                value[counter][1] = herd[i][2];
                value[counter][2] = herd[i][3];
            }
        }
        return  value;
    }
    /*

    Create a matrix of cows by their milk value

    */
    public static double [][] select_cows_by_milk_value(int number){
//        double [][] value = new double[number][10];
//        final double[][] copy_herd = new double[herd.length][];
//        for (int i = 0; i < herd.length; i++) {
//            copy_herd[i] = Arrays.copyOf(herd[i], herd[i].length);
//        }
//        double [][] returnVal = get_sorted_herd(copy_herd, 44,0,0, false);
//        // Invert to get the correct order
//        for (int i = 0; i < returnVal.length/2; i++)
//        {
//            double[] temp = returnVal[i];
//            returnVal[i] = returnVal[returnVal.length-1 - i];
//            returnVal[returnVal.length-1 - i] = temp;
//        }
//        return  returnVal;
        double [][] value = new double[getSize()][11];
        int counter = 0;
        for(int i = 0; i < getSize(); i++){
            if(herd[i][3]==1||herd[i][3]==2||herd[i][3]==3||herd[i][3]==4){
                value[counter][0] = herd[i][1];
                value[counter][1] = herd[i][2];
                value[counter][2] = herd[i][3];
                value[counter][3] = herd[i][9];
                value[counter][4] = herd[i][10];
                value[counter][5] = herd[i][7];
                value[counter][6] = herd[i][8];
                value[counter][10] = herd[i][44];
                counter++;
            }
        }
        return  value;
    }
    /*

    Create a matrix of cows by their ages

    */
    public static double [][] select_cows_by_age(){
        double [][] value = new double[getSize()][10];
        int counter = 0;
        for(int i = 0; i < getSize(); i++){
            if(herd[i][3]==1||herd[i][3]==2||herd[i][3]==3||herd[i][3]==4){
                value[counter][0] = herd[i][1];
                value[counter][1] = herd[i][2];
                value[counter][2] = herd[i][3];
                value[counter][3] = herd[i][9];
                value[counter][4] = herd[i][10];
                value[counter][5] = herd[i][7];
                value[counter][6] = herd[i][8];
                counter++;
            }
        }
        return  value;
    }
    // Create new matrix of all cows
    // matrix columns: 1 cow ID; 2 Age; 3 Parity; 4 Pregnant days;
    // 5 DIM.
    public static double [][] select_susceptible_cows(){
        double [][] value = new double[getSize()][5];
        int counter = 0;
        for(int i = 0; i < getSize(); i++){
            if(herd[i][5]>=1){
                value[counter][0] = herd[i][1];
                value[counter][1] = herd[i][2];
                value[counter][2] = herd[i][5];
                value[counter][3] = herd[i][7];
                value[counter][4] = herd[i][9];
            }
        }
        return  value;
    }
    // Get the list_cow_s
    public static int size_of_list_cow;
    public static double [][] get_ListCowS(){
        size_of_list_cow =0;
        double [][] value = new double[getSize()][3];
        int counter = 0;
        for(int i = 0; i < getSize(); i++){
            if(herd[i][3] ==1.0){
                value[counter][0] = herd[i][1];
                value[counter][1] = herd[i][3];
                value[counter][2] = herd[i][5];
                counter++;
            }
        }
        size_of_list_cow = counter;
        return  value;
    }
    // Get the list_cow_s
    public static int size_of_list_cow2;
    public static double [][] get_ListCowS2(){
        double [][] value = new double[getSize()][3];
        int counter = 0;
        for(int i = 0; i < getSize(); i++){
            if(herd[i][3]==1 ||herd[i][3]==2 ||herd[i][3]==3 ||herd[i][3]==4){
                value[counter][0] = herd[i][1];
                value[counter][1] = herd[i][3];
                value[counter][2] = herd[i][5];
                counter++;
            }
        }
        size_of_list_cow2 = counter;
        return  value;
    }
    // Get the death herd.
    public static int size_of_death;
    public static double[][] death_herd;
    public static double [][] get_DeathsHerd() {
        double[][] value = new double[getSize()][43];
        int counter = 0;
        for (int i = 0; i < getSize(); i++) {
            if (herd[i][14] > 0) {
                value[counter] = herd[i];
                counter++;
            }
        }
        size_of_death = counter;
        death_herd = value;
        return value;
    }

        // return the data sample by sample and number
    public static double [] data_sample(double[][] input, int num, int col, int com_col){
        // Have a random generator
        Random rand = new Random();
        double [] value = new double[num];
        int counter = 0;
        while(counter<num){
            if(com_col!=0 && input[rand.nextInt(size_of_list_cow2)][2] >= com_col){
                value[counter] = input[rand.nextInt(input.length)][col];
                counter++;
            }else if(com_col==0) {
                value[counter] = input[rand.nextInt(size_of_list_cow)][col];
                counter++;
            }
        }
        return  value;
    }

    // Get rid of cow by id_array, for time-complexity, we note the cow's ID to -1 and remove them together by the end of Simulation loop
    public static void  get_rid_of_cow_by_ID(double[][] input_array,double[] id_array, int is_herd){
        // Have a random generator
        int counter =0;
        int size;
        if(is_herd==1){
            size = getSize();
        }else{
            size = input_array.length;
        }
        for(int i =0; i < id_array.length; i++) {
            for (int j = 0; j < size; j++) {
                if (input_array[j][1] == id_array[i]) {
                    // Set this cow's ID to -1 and move to next id
                    counter++;
                    input_array[j][1] = -1;
                    break;
                }
            }
        }
    }
    // get the age all array
    public static double[][]  get_age_all(int[] parityScenarioWarmUp){
        // Have a random generator
        Random generator=new Random();
        int counter =0;
        int max_length =0;
        for(int i =0; i < parityScenarioWarmUp.length; i++){
            if(parityScenarioWarmUp[i]!=0){
                if(parityScenarioWarmUp[i]>max_length){
                    max_length = (int)parityScenarioWarmUp[i];
                }
                counter++;
            }
        }
        double [][] value = new double[counter][max_length];
        counter = 0;
        for(int i =0; i < parityScenarioWarmUp.length; i++){
            if(parityScenarioWarmUp[i]!=0){
                for(int j =  0; j< (int)parityScenarioWarmUp[i]; j++){
                    if(i==0){
                        value[counter][j] = generator.nextInt(31)+720;
                    }
                    if(i==1){
                        value[counter][j] = generator.nextInt(30)+1061;
                    }
                    if(i==2){
                        value[counter][j] = generator.nextInt(30)+1401;
                    }
                    if(i==3){
                        value[counter][j] = generator.nextInt(30)+1741;
                    }
                    if(i==4){
                        value[counter][j] = generator.nextInt(20)+2081;
                    }
                }
                counter++;
            }
        }
        return value;

    }
    // Append new_herd to the end of the herd array
    public static void  append_to_end(double[][] new_herd){
        // Have a random generator
        int size = getSize();
        for(int i = 0; i<new_herd.length; i++){

            herd[i+size] = new_herd[i];
            size_of_herd++;
        }
    }

    // Clean up all the herd with -1 ID
    public static double[][] clean_up_herd(){
        int new_size = 0;
        double [][]new_herd = new double[3000][45];
        for(int i =0; i < getSize(); i++){
            if(herd[i][1]!=-1){
                new_herd[new_size] = herd[i];
                new_size++;
            }
        }
        herd = new_herd;
        size_of_herd = new_size;
        return herd;
    }

    // get the mean of a specific column
    public static  double get_mean_of_column(int col) {
        double value =0;
        int counter =0;
        for (int i =0; i < getSize(); i++){
            if(herd[i][col]>0){
                value += herd[i][col];
                counter++;
            }
        }
        return value/counter;
    }

    //  count Death animal
    public static  int[] counter_death(int[] DeadAnimals) {
        for(int i=0; i < size_of_death; i++){
            if(((int)death_herd[i][14])>0 &&((int)death_herd[i][14])<=14){
                DeadAnimals[((int)death_herd[i][14])-1]++;
            }
        }
        return DeadAnimals;
    }

    //  Calculate Gross profit
    public static void gross_profit() {
        for(int i=0; i < getSize(); i++){
            herd[i][24] = herd[i][13]-herd[i][23]-herd[i][22]-herd[i][12];//  Gross Profit per animal/day Nominal Milk Revenue - Feed Cost - NonFeedCosts
        }
    }
    //  Calculate Gross profit
    public static int[] get_NumDeath(int[] NumDeath) {
        for(int i=0; i < size_of_death; i++){
            NumDeath[(int)death_herd[i][14]-1]++;
        }
        return NumDeath;
    }

    //get the TotWeightCullCowsLb
    public static double get_TotWeightCullCowsLb(){
        double value =0;
        for (int i =0; i < size_of_herd; i++){
            if(herd[i][14]==4||herd[i][14]==5||herd[i][14]==6||herd[i][14]==9||herd[i][14]==12||herd[i][14]==14){
                value+=herd[i][21];
            }
        }
        return 2.2*value;
    }

    //get the TotWeightCullCowsLb
    public static double[] get_NumCullInf(){
        double[] value = new double[8];
        boolean isEmpty = true;
        for(int i=0; i < size_of_death; i++){
            value[(int)death_herd[i][3]-1]++;
            isEmpty = false;
        }
        if(isEmpty){
            value = new double[4];
        }
        return value;
    }

    /**
     * @param col
     * @return
     */
    public static double get_sum_col(int col){
        double value =0;
        for(int i =0; i < size_of_herd; i++){
            value+=herd[i][col];
        }
        return value;
    }

    public static double ME305(int dim, int parity, double sumMilkDIM){
        int p =  parity > 1 ? 1: 0;
        // Question: What is milkD. If it is the production of milk for a cow in each day. How can we get the data?
        double mD_sum = mD(p, dim);
        double milk305;

        if(dim > 305){
            milk305= sumMilkDIM;
        }else{
            milk305 = sumMilkDIM+ mD_sum;
        }
        return (parity>1 ? milk305 : milk305*1.49);
    }

    public static double mD(int p, int dim){
        double sum = 0;
        for(int i = dim+1; i < 305; i++){
            sum += 42.56+9.6*p-(0.02+0.03*p)*i-(27.28-0.29*p)*Math.exp(-0.1*i);
        }
        return sum;
    }


}
