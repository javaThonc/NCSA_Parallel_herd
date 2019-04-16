import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.lang.*;

import static java.lang.Math.ceil;

public class herdParameter {
    //// Parameters and variables of the Model
    static final int MaxSim= 100 ; // Number of iterations

    // For Tb only: WarmUp= 3000, Stability= 0
    static final int WarmUp= 0; // Set to 0 if starting simulation from the same herd.
    static final int StabilityTime= 0; // Set to 0 if starting simulation from the same herd.
    // Can change this value
    static final int AnalysisTime= 7300; // after WarmUp+Stability, do the analysis for this time (in days): 20 Yrs= 7300 days

    public static final int T = StabilityTime + WarmUp + AnalysisTime; // Total days of simulation including warmup and stability time.
    static final double [][] Result = new double[MaxSim][32]; //Initialize matrix to store the main results for each iteration
    static final double colherd= 41; // Number of columns in matrix Herd (main matrix of results).
    static final double [][][] ResultsWHT= new double[40][12][MaxSim]; // Initialize matrix to store the results of the whole herd tests (for bTB contol) for each iteration
    //=============================================================================================================================================================
    //// Infection Transmission Parameters

    // Tranmission parameter for MAP
    static final double beta_aa= 0.00047; // Transmission Parameter Adult-Adult
    static final double beta_h= 0.0006; // Transmission Parameter Heifer-Heifer
    static final double beta_ac= 31.8;  // Transmission Parameter Adult-Calf
    static final double beta_y1= 1.0;     // transmission param between low shedders and susceptibles (S-Y1)
    static final double beta_y2= 10;    // transmission param between high shedders and susceptibles (S-Y2)

    static final double beta_c= 9.2 ;
    static final double ExitRateLtoY1= 0.0018/1.1; // Exit rate
    static final double ExitRateY1toY2= 0.000904/1.1;
    static final double MilkPrice= 0.427-0.286; // 5-yr average:
    // MAP Progression Rate
    // Natural culling rate
    static final double nat_cull_prob_same= 0.000132;       // Culling from diseases and accidents.
    static final double NatCullProbHeifers= 0.0000192*8;
    static final double NatCullProbCalves=  0.000304*8;     // Natural Daily Culling Prob for Calves 0.00041, model
    static final double factorcull= .3;     // factor to modify culling rate for cows

    // Maximum parity and pregnancy parameters
    static final int AIattempts= 8;  // Number of maximum failed insemination Attempts before culling
    static final int MaxParity= 9;   // Number of parity before culling
    static final double prob_pregInput= 0.18; // Prob of pregnancy of Heifers. Parity 0
    static final double annualProb= 0.70;   // Annulal Prob of Pregnancy for Cows

    static final double volculloption= 0; // Switch to activate voluntary cull animals (yes/no), as a probability.
    static final double VolCullProbHeifers= 0.00; // .00004; 0.00004
    static final double VolCullProbCalves=  0.00; //  .000053; 0.0002
    static final double vol_cull_prob_same= 0.00 ; // .0000; 0.00003; //

    // MAP Infection and progresison parameters
    // Allow Infection to Progress? Yes/No= 1/0. These are switches.
    static final double AllowInfection= 1;   // This is for ALL infection transmissions of MAP (Vertical and Horizontal)
    // // Factors that modify Vertical Transmission.
    // // SET TO 0 if NO VERTICAL INFECTION.
    static final double FactorVertical_L= 1;             // Factor to modify Latent-Calf Vertical Transmission rate.
    static final double FactorVertical_Y1= 1;            // Factor to modify Low Shedders-Calf Vertical Transmission rate.
    static final double FactorVertical_Y2= 1;            // Factor to modify High Shedders-Calf Vertical Transmission rate.
    // Factors that modify Progression Rate (to Y1 and Y2)
    static final double FactorProgtoY1= 1;              // Factor to midify Progression rate from Latent to Y1. This may be lower than 1 due to vaccines
    static final double FactorProgtoY2= 1;              // Factor to midify Progression rate from Latent to Y2. This may be lower than 1 due to vaccines

    // MAP Testing: FC and Elisa;
    // Select whether the model allows for testing, then select frequency.

    // TEST AND CULL
    static final int fcTest= 1; // Test FC, no false negatives
    static final int elisaTest= 1; // Test Elisa
    static final int FrequencyTest= 1; //0.5; // Annual test, values of rates are for Annual (0.5), Every 6 months (1).
    static final int costElisa= 6;
    static final int costFC= 36;

    static final double TestSpecificityElisa= 0.97; //0.95; // Tests Specificity of Elisa. (True Negatives)
    static final double TestSpecificityFC= 1; // Tests Specificity of FC. IT has a specificity of 1.
    static final double TestSensitivityElisaY1= 0.24; // 0.3; // Test sensitivity for Low Shedders on Elisa Test (True Positives)
    static final double TestSensitivityFCY1= 0.5; // Test sensitivity for Low Shedders on FC Test
    static final double TestSensitivityElisaY2= 0.78; //0.75; // Test sensitivity for High Shedders on Elisa Test
    static final double TestSensitivityFCY2= 0.9; // Test sensitivity for High Shedders on FC Test

    // MILK PRODUCTION Y2 (High Shedders of MAP)
    static final double PregRateDecreaseY2= 0.9; // Decrease Pregnancy rate by 10// for High Shedders

    // Voluntary Cull Cows if their number is greater than the limit in the herd
    static final int VoluntCullOption= 1; // cull cows to keep number to 1000. (Y=1/ N=0), for the WarmUp period only
    static  int herdLimit= 1000; // Capacity of the number of cows in the herd (1000)

    // VoluntaryCulling of Calves
    // Keep the number of calves in the calving compartment to a limit, so that
    // the average number of cows is stable.
    // Turn Switch ON (=1) if Using Voluntary Calves Culling
    static final int VoluntaryCalfCull= 1; // Set to 1 if voluntary culling calves (to sell)
    static final int  MilkProdSame= 0; // 1= Make production of Susceptible = Latent
    static final int groupLact= 1; // Separage cows into groups according to their lactation stage.
    //=============================================================================================================================================================
    //// PARAMETERS FOR INITIALIZATION OF MAP INFECTION

    // MAP Parameters
    // Set to 0 for ALL SUSCEPTIBLE (Healthy)
    // The following are for ScenarioWarmUp 1 Parameter
    static final int NewInfectedType= 4; // Infection Status (2-4) of the Newly Infected Cows (Start Scenario with this infection status)
    static final int[] parityScenarioWarmUp = {0,0,100,0,0} ; // Scenario 1 only. Number of New Animals in each parity (1-5)
    // Total sum is the number of Infected Cows
    // ScenarioWarmUp 2 Parameter (20//= 0.8; 0.7; 0.2; 0.1); 60//= // 0.5,0.5,0.3,0.2)
    static final double PercInfected=   0.5; // 0.8; //0.5 ONLY FOR ScenationWarmUp= 2. Percentage of Initial Infected animals (L+Y1+Y2)
    static final double ScenarioPercL=  0.5; //0.7; //0.5 Percentage of Infected Cows that are L
    static final double ScenarioPercY1= 0.3; //0.2; //0.3 Percentage of Infected Cows that are Y1
    static final double ScenarioPercY2= 0.2; //0.1; //0.2 Percentage of Infected Cows that are Y2
    // These values set to 0.
    static final int HeiferHoriz= 0;  // Allow Heifer-Heifer Horizontal Transmission
    static final int HorTransSusc= 0; // Allow Horizontal Transmission to S (A-A)

    // Allow Infection to Progress? Yes/No= 1/0. These are switches.

    // MAP Parameters
    static final int AdultCalfHoriz= 1; // Allow Horizontal Transmission from Adult to Calf. This is Environment Effect
    static final int CalfCalfHoriz= 1; // Calf Calf Horizontal Transmission

    // Factors that modify Horizontal Transmission
    static final int FactorAA= 1;        // Factor to modify Adult-Adult Horizontal Transmission rate. This may be lower than 1 due to hygene
    static final int FactorHH= 1;        // Factor to modify Heifer-Heifer Horizontal Transmission rate. This may be lower than 1 due to hygene
    static final int FactorCC= 1;        // Factor to modify Calf-Calf Horizontal Transmission rate. This may be lower than 1 due to hygene
    static final int FactorAC= 1;        // Factor to modify Adult-Calf Horizontal Transmission rate (0-1). low- high hygiene (0.66- 0.33)

    // Vertical Transmission Parameters
    static final int VerticalTrans= 1; // Allow for Vertical, In Utero Transmission.
    static final double vertical_L=  0.15; // Vh, Vertical Transmission Prob. from Latent Cow to Calf. (Infected Calf prob)
    static final double vertical_Y1= 0.15; // Vy1, Vertical Transmission Prob. from Low Shedder Cow to Calf. (Infected Calf prob)
    static final double vertical_Y2= 0.17; // Vy2, Vertical Transmission Prob. from High Shedder Cow to Calf. (Infected Calf prob)
    static final double  ColostrumS= 0, ColostrumL= 0.3, ColostrumY1= 0.3, ColostrumY2= 0.34; // Transmission Prob Through Colostrum
    //////////////////////////////////////////////////////////////
    // Include MAP in the herd.
    static final int ScenarioWarmUp= 0; // MAP Yes =1, No = 0, no infection.
    static  int ControlCullTest= 0; // Yes 1, No 0. Allow for Elisa and FC Test.
    static  int allsusceptible= 0; // 1 if the herd is all susceptible,


    static  int ParityTest= 4; // Test this parity up
    static  int TimetoCull= 60; // Days to cull positive FC test
    static  int ControlCullLowMilk= 0; // Yes, No. Cull if milk production is below normal for 90 days.
    static  int LowMilkDays= 60; // Cull after these days of low milk
    static  int LowMilkCullNow= 1; // 1 then cull immediate; 0 cull at end of lactation
    static  int ControlCullCalvesTest= 1; // Test and Cull Cows, AND its latest calf
////////////////////////////////////////////////////////////////////////////////

    static final int DMICost= 0; //MilkCost/0.64; // /.64 to match cost of DMI per lactatin cow.
    static final int USDAparam= 0; // 1 if using the USDA test parameter values, 0 if using Alternative values
    static double SeCFTBetaA, SeCFTBetaB, SpCFTBetaA, SpCFTBetaB, SeCCTBetaA, SeCCTBetaB, SpCCTBetaA, SpCCTBetaB, SeNecBetaA, SeNecBetaB, SpNecBetaA, SpNecBetaB,
            SeHistBetaA, SeHistBetaB, SpHistBetaA, SpHistBetaB, SeCultureBetaA, SeCultureBetaB;
    static double Si;
    static double Li ;
    static double Y1_i ;
    static double Y2_i ;
    static double CS_i ;
    static double CI_i ; // Infected Calves
    static double HS_i ; // Susceptible Heifers
    static double HI_i; // Infected Heifers
    static double C_Infrate;
    static double H_Inf;
    static double H_Infrate;

    // =====================================================================================================================================
    //// THis shound be betatest=1
    static final int betatest= 1; // use beta distribution. IT is either betatest= 1 OR perttest= 1.
    static final int perttest= 0; // Switch to use PERT distribution, otherwise use AVP. IT is either betatest= 1 OR perttest= 1.


    // Use min-mode-max for simulating using PERT distribution (gamma=1, shape param)
    // Mode values were estimated in order to arrive at the average values from
    // the literature
    static final double SpCFTmin= 0.755, SpCFTmode=0.99, SpCFTmax=0.99, // Specificity of CFT
            SeCFTmin= 0.632, SeCFTmode=0.851, SeCFTmax=1, // Sensitivity of CFT
            SpCCTmin= 0.788, SpCCTmode=1, SpCCTmax=1, // Specificity of CCT
            SeCCTmin= 0.750, SeCCTmode=0.966, SeCCTmax=0.995, // Sensitivity of CCT
            SePMmin= 0.285, SePMmode=0.52, SePMmax=0.95, // Sensitivity of Regular PostMortem Test
            SePCRmin= 0.8, SePCRmode=1, SePCRmax=1, // Sensitivity of Enhanced PostMortem Test

            pertgammaSeCFT= 4, pertgammaSeCCT= 4,
            pertgammaSpCFT= 60, // 9 to get the literature values, which gives 3.6// false pos. 50 to get 1.8// FP
            pertgammaSpCCT= 60, // 40 to get the literature values, which gives 3.6// false pos. 50 to get 1.8// FP
            pertgammaSePCR= 200, pertgammaSePM= 4;

    static final double SpCFT= 0.968;   // Specificity of CFT Test, Caudal Fold Test (Becky, 2013) De la Rua-Domenech et al. 2006.
    static final double SpCCT= 0.995;   // Specificity of CCT Test, Comparative Cervical Test (Becky, 2013)
    static final double SeCFT= 0.839;   // Sensitivity of CFT to Infectious or Reactor cattel (Becky, 2013)
    static final double SeCCT= 0.935;   // Sensitivity of CCT to Infectious or Reactor cattel (Becky, 2013)
    static final double SePM=  0.55;     // Sensitivity of Post Mortem inspection to Infectious or Reactor to detect Tb
    static final double SePCR= 1.0;       // Sensitivity of Enhanced Post-Mortem inspection to Infectious or Reactor ot detect Tb
    static final double allSerial= 0; // Default, use parallel in removal test, phase 1 (Current USDA)

    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // New Protocol: 2 WHT removal; 1 WHT verification, 3-5 WHT assurance
    // Rerun Scenarion 12 for 70 iterations (May,4)
    // Scenarios parameters: change these values at the conditional blocks below
    // idcalf= 0; // ID of a female calf to follow to estimate raisin costs.
    static final int NumConsNegWHTRemoval= 2; // Num consecutuve Neg WHT to leave Removal phase
    static final int  NumNegWHTClear= 8; // OLD Protocol. Number of consecutive WHT negative in order to be declared TB free (lift quarantine) removal + 1 verification + 5 assurance

    // The initial cost of calf (150)
    static final int CostCalf = 150; // Cost of a newborn calf alive (Male or Female), NOT MARKET VALUE. (Karszes,pg 3)

    static int MaleCalfPrice = 150; // Price of a newborn male calf
    static final int FemaleCalfPrice = 250; // Price of a female calf (they are culled once their number pass a limit)
    // (100 margin)
    static final int CulledCowPrice = 660; // Price of a culled cow due unable to get pregnant or over parity. av cull weight: 1600 Lb, at $100/cwt (50// dressing)
    static final int MatureWeight = 600; // Mature Weight of a Pregnant Cow in Kg.
    static final double CullCowPriceperKg = 1.1; // estimated from the AMS.USDA Reports
    static final double WeightLossY2Perc = 0.10; // weight loss of Y2 compared to non Y2. (10//)
    static final double HeiferReplCost = 2200 * 1.1; // Costs estimated from Model. (// margin)

    static final int managementChanges = 0; // 1/0 if incorporting management changes or interventions.
    static final int NewManagementCostperHead = 0; //55/365; // Cost of changing management practices per animal per day
    static final int CostInterventionCows = 0;    // Cost of Intervention strategy per Cow PER DAY
    static final int CostInterventionHeifers = 0; // Cost of Intervention strategy per Heifer PER DAY
    static final int CostInterventionCalves = 0;  // Cost of Intervention strategy per Calf PER DAY

    static final double HighShedFeedCostFactorMilkProd = 1.00; // Multiplicative factor in feed cost for high shedders under milk production
    static final double HighShedFeedCostFactorNoMilk = 1.00;  // Multiplicative factor in feed cost for high shedders under NO milk production

    static final int InsemCost = 20; // USD. Insemination Cost per service (15 Semen + 5 Vet)
    static final int PregnancyDiagnosis = 8; // USD. Cost of Pregnancy diagnosis,42-60-220 days after Insemination

    static final double FixedVarsCostDay = 2.5; // This is non-feed variable costs and fixed costs per cow per day > 720 days
    static final int maxMAPDays = 300; // Maximum number of MAP DAYS for the Milk Produciton Function

    static final  double irate = 0.05; // Set Yearly discount rate
    static final double dailyirate = 1 - Math.exp(-irate / 365); // Daily discount rate
    // Testing variables, Current Protocol USDA
    static final int TestingPhase = 0; // Initialize
    static final int lastRemovalday = 0; // initialization of time of last removal test
    static final int timeHeifersIndemnityEnter = 0; // Initialization time to include indemnity heifers

    static int Beta = 0;
}

