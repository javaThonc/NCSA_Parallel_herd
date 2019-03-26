// cow herd disease simulation
public class herd2 {
	static final int HERDMAXSIZE = 1000000;
	static int simtime = 11; //22*365;	// 22 years with 2 year burn-in

	// coefficients from Table 2 on the paper
	static final double h1 = 0.0013;	// disease progression rate from latent H to how shedding Y1 daily probability
	static final double y1 =  0.00077; //  disease progression rate  from low  shedding Y1 to  high shedding  Y2, daily probability
	static final double vh = 0.15;		// proportion of calves from latent animals infected at birth
	static final double vy1 = 0.15;		// proportion of calves from low-shedding animals infected at birth
	static final double vy2 = 0.17;		// proportion of calves from high-shedding animals infected at birth
	static final double betaa = 0.05;	// adult-to-adult transmission coefficient
	static final double betaalpha = 0.383;	// adult-to-calf transmission coefficient
	static final double betac = 0.0025;	// calf-to-calf transmission
	static final double betam = 0.072;	// adult-to-calf transmission via colostru, daily probability
	static final double betah = 0.001;	// heifer-to-heifer transmission
	static final double betay1= 0.00547945;	// transmission rate between low shedders Y1 and susceptible S, 2/year
	static final double betay2= 0.05479452;	// transmission rate between high shedders Y1 and susceptible S, 20/year

	static final int BORNCALF = 0;	// calf
	static final int VWP = 1; 	// voluntary waiting period
	static final int INST = 2;	// insemination
	static final int PREGNANT = 3;	// pregmant
	static final int NOTPREGNANT = 4;	// nonpregnant
	static final int DP = 5;		// dryperiod
	static final int CALVING = 6;	// calving;


	public static void main(String[] args) {

		int[] parity;		// 8 parities fo cows
		parity = new int[HERDMAXSIZE];
		double[] cows;		// age of cows
		cows = new double[HERDMAXSIZE];
		int[] compartment;
		compartment = new int[HERDMAXSIZE];
		int[] diseasestage;
		diseasestage = new int[HERDMAXSIZE];
		int[] lactationstage;
		lactationstage = new int[HERDMAXSIZE];
		int[] livingstatus;
		livingstatus = new int[HERDMAXSIZE];

		int numcows, nextnumcows;

		// initialization
		// parity 1, lactation 0
		// 375 per Fig 5, and 60 days for calf & 720-60 days for heifers
		int tncows;

		/* no heifer and no calf during the initialization for the 2 year burn-in
		tncows=0;	// temporary number of cows
		for (int i=0; i<tncows; i++) {
		int randomInt = (int) Math.ceil(Math.random() * 720);
		cows[i] = randomInt;	// assign the age between 1 and 720 inclusive

		if (cows[i] < 61) {
		compartment[i] = 0;	// this is a calf
		diseasestage[i] = 0;	// susceptible calf
		} else {	// >=61 & <= 720 days, this is a heifer
		compartment[i] = 1;	// this is a heifer
		diseasestage[i] = 0;	// susceptible heifer
		}
		}
		nextnumcows=tncows;
		*/

		// parity 1
		nextnumcows=0;
		tncows=450;		// 45% of 1000 initial adult cows vs 375
		for (int i=nextnumcows; i<nextnumcows+tncows; i++) {
			parity[i] = 1;
			livingstatus[i]=1;

			int randomInt = (int) Math.ceil(Math.random() * 340);	// 340 days for parity 1
			cows[i] = randomInt + 720;

			// this is an adult cow, milking
			compartment[i] = 2;

			// assign disease stage for adult cow
			double ds;
			ds = Math.random();
			if (ds < 0.04) {
				diseasestage[i] = 1; // latent
			} else if (ds < 0.08) {
				diseasestage[i] = 2; // low shedder
			} else if (ds < 0.10) {
				diseasestage[i] = 3; // high shedder
			} else {
				diseasestage[i] = 0; // susceptible
			}
		}
		nextnumcows=nextnumcows+tncows;

		// parity 2
		tncows=250;		// 25% of 1000
		for (int i=nextnumcows; i<nextnumcows+tncows; i++) {
			parity[i] = 2;
			livingstatus[i]=1;

			int randomInt = (int) Math.ceil(Math.random() * 340);	// 340 days for parity 2
			cows[i] = randomInt + 720 + 340;	// assign age

			// this is an adult cow, milking
			compartment[i] = 2;

			// assign disease stage for adult cow
			double ds;
			ds = Math.random();
			if (ds < 0.04) {
				diseasestage[i] = 1; // latent
			} else if (ds < 0.08) {
				diseasestage[i] = 2; // low shedder
			} else if (ds < 0.10) {
				diseasestage[i] = 3; // high shedder
			} else {
				diseasestage[i] = 0; // susceptible
			}
		}
		nextnumcows=nextnumcows+tncows;

		tncows=140;
		for (int i=nextnumcows; i<nextnumcows+tncows; i++) {
			parity[i] = 3;
			livingstatus[i]=1;

			int randomInt = (int) Math.ceil(Math.random() * 340);	// 340 days for parity 3
			cows[i] = randomInt + 720 + 340 + 340;	// assign age

			// this is an adult cow, milking
			compartment[i] = 2;

			// assign disease stage for adult cow
			double ds;
			ds = Math.random();
			if (ds < 0.04) {
				diseasestage[i] = 1; // latent
			} else if (ds < 0.08) {
				diseasestage[i] = 2; // low shedder
			} else if (ds < 0.10) {
				diseasestage[i] = 3; // high shedder
			} else {
				diseasestage[i] = 0; // susceptible
			}
		}
		nextnumcows=nextnumcows+tncows;

		tncows=80;
		for (int i=nextnumcows; i<nextnumcows+tncows; i++) {
			parity[i] = 4;
			livingstatus[i]=1;

			int randomInt = (int) Math.ceil(Math.random() * 340);	// 340 days for parity 4
			cows[i] = randomInt + 720 + 340 + 340 + 340;	// assign age

			// this is an adult cow, milking
			compartment[i] = 2;

			// assign disease stage for adult cow
			double ds;
			ds = Math.random();
			if (ds < 0.04) {
				diseasestage[i] = 1; // latent
			} else if (ds < 0.08) {
				diseasestage[i] = 2; // low shedder
			} else if (ds < 0.10) {
				diseasestage[i] = 3; // high shedder
			} else {
				diseasestage[i] = 0; // susceptible
			}
		}
		nextnumcows=nextnumcows+tncows;

		// parity 5
		tncows=50;
		for (int i=nextnumcows; i<nextnumcows+tncows; i++) {
			parity[i]=5;
			livingstatus[i]=1;

			int randomInt = (int) Math.ceil(Math.random() * 340);	// 340 days for parity 5
			cows[i] = randomInt + 720 + 340 + 340 + 340 + 340;	// assign age

			// this is an adult cow, milking
			compartment[i] = 2;

			// assign disease stage for adult cow
			double ds;
			ds = Math.random();
			if (ds < 0.04) {
				diseasestage[i] = 1; // latent
			} else if (ds < 0.08) {
				diseasestage[i] = 2; // low shedder
			} else if (ds < 0.10) {
				diseasestage[i] = 3; // high shedder
			} else {
				diseasestage[i] = 0; // susceptible
			}
		}
		nextnumcows=nextnumcows+tncows;

		// parity 6
		tncows=20; // 30
		for (int i=nextnumcows; i<nextnumcows+tncows; i++) {
			parity[i] = 6;
			livingstatus[i]=1;

			int randomInt = (int) Math.ceil(Math.random() * 340);	// 340 days for parity 6
			cows[i] = randomInt + 720 + 340 + 340 + 340 + 340 + 340;	// assign age

			// this is an adult cow, milking
			compartment[i] = 2;

			// assign disease stage for adult cow
			double ds;
			ds = Math.random();
			if (ds < 0.04) {
				diseasestage[i] = 1; // latent
			} else if (ds < 0.08) {
				diseasestage[i] = 2; // low shedder
			} else if (ds < 0.10) {
				diseasestage[i] = 3; // high shedder
			} else {
				diseasestage[i] = 0; // susceptible
			}
		}
		nextnumcows=nextnumcows+tncows;

		// parity 7
		tncows=8;  // 10
		for (int i=nextnumcows; i<nextnumcows+tncows; i++) {
			parity[i] = 7;
			livingstatus[i]=1;
			int randomInt = (int) Math.ceil(Math.random() * 340);	// 340 days for parity 7
			cows[i] = randomInt + 720 + 340 + 340 + 340 + 340 + 340 + 340;	// assign age

			// this is an adult cow, milking
			compartment[i] = 2;

			// assign disease stage for adult cow
			double ds;
			ds = Math.random();
			if (ds < 0.04) {
				diseasestage[i] = 1; // latent
			} else if (ds < 0.08) {
				diseasestage[i] = 2; // low shedder
			} else if (ds < 0.10) {
				diseasestage[i] = 3; // high shedder
			} else {
				diseasestage[i] = 0; // susceptible
			}
		}
		nextnumcows=nextnumcows+tncows;

		// parity 8
		tncows=2;
		for (int i=nextnumcows; i<nextnumcows+tncows; i++) {
			parity[i]=8;
			livingstatus[i]=1;
			int randomInt = (int) Math.ceil(Math.random() * 340);	// 340 days for parity 8
			cows[i] = randomInt + 720 + 340 + 340 + 340 + 340 + 340 + 340 + 340;	// assign age

			// this is an adult cow, milking
			compartment[i] = 2;

			// assign disease stage for adult cow
			double ds;
			ds = Math.random();
			if (ds < 0.04) {
				diseasestage[i] = 1; // latent
			} else if (ds < 0.08) {
				diseasestage[i] = 2; // low shedder
			} else if (ds < 0.10) {
				diseasestage[i] = 3; // high shedder
			} else {
				diseasestage[i] = 0; // susceptible
			}
		}


		nextnumcows=nextnumcows+tncows;
		numcows=nextnumcows;


		/*
		// print all cow age and compartment
		for (int i = 0; i < numcows; i++) {
		System.out.print(cows[i] + " ");
		System.out.println(compartment[i]);
		}
		*/

		tncows=numcows;	// counter of living cows
		System.out.println(tncows);

		// simulation loop
		for (int t = 0; t < simtime; t++) {
			int n, y1, y2;
			int nc, cs, ci;
			int ny, ys, yi;
			n = y1 = y2 = 0;
			nc = cs = ci = 0;
			ny = ys = yi = 0;
			System.out.println("t " + t);

			// count numbers

			for(int i=0; i < numcows; i++) {
				if (0==livingstatus[i])
				{
					continue;		// skip a dead cow
				}

				// count the number of adult, low shedder, highshedder
				if (2 == compartment[i]) { // adult
					n++;
					if (2 == diseasestage[i]) { // low shedder
						y1++;
					} else if (3 == diseasestage[i]) { // high shedder
						y2++;
					}
				}

				// number of calf, susceptible calf, infected calf
				if (0 == compartment[i]) { // calf
					nc++;
					if (5 == diseasestage[i]) { // infected calf
						ci++;
					} else if (0 == diseasestage[i]) { // susceptible calf
						cs++;
					}
				}

				// number of heifer, susceptible heifer, infected heifer
				if (1 == compartment[i]) { // heifer
					ny++;
					if (5 == diseasestage[i]) { // infected heifer
						yi++;
					} else if (0 == diseasestage[i]) { // susceptible heifer
						ys++;
					}

				}

				//			System.out.print(compartment[i] + " " + diseasestage[i] + "   ");
			}


			System.out.println();

			if (10==t)
			{
			System.out.print("adult lo hi ");
			System.out.print(n);
			System.out.print(" ");
			System.out.print(y1);
			System.out.print(" ");
			System.out.println(y2);
			System.out.print("heifer inf sus ");
			System.out.print(ny);
			System.out.print(" ");
			System.out.print(yi);
			System.out.print(" ");
			System.out.println(ys);
			System.out.print("calf inf sus ");
			System.out.print(nc);
			System.out.print(" ");
			System.out.print(ci);
			System.out.print(" ");
			System.out.println(cs);
			}

			// calculate infection rate
			double infadultadult, infadultcalf, infcalfcalf, infheiferheifer;
			infadultadult = betaa * (betay1 * y1 + betay2 * y2) / n;
			infadultcalf = betaalpha * (betay1 * y1 + betay2 * y2) / nc;
			infcalfcalf = betac * ci / nc;
			infheiferheifer = betah * yi / ny;


			for(int i=0; i < numcows; i++) {

				if (0==livingstatus[i])
				{
					continue;			// skip dead cows
				}

				// disease progression
				double dp;

				dp = Math.random();
				// adult-adult
				if (2 == compartment[i]) {	// adult
					if (0 == diseasestage[i]) {	// susceptible S
						if (dp < infadultadult)
							diseasestage[i] = 1;	// latent/hidden H
					} else if (1 == diseasestage[i]) {
						if (dp < h1)
							diseasestage[i] = 2;	// low shedder
					} else if (2 == diseasestage[i]) {
						if (dp < y1)
							diseasestage[i] = 3;	// high shedder
					}
				}

				dp = Math.random();
				// heifer-heifer
				if (1 == compartment[i]) {	// heifer
					if (0 == diseasestage[i]) { // susceptible
						if (dp < infheiferheifer)
							diseasestage[i] = 5;	// infected
					}
				}

				dp = Math.random();
				// adult-calf
				// calf-calf
				if (0 == compartment[i]) { 	// calf
					if (0 == diseasestage[i]) { 	// susceptible
						if (cows[i] <= 2) {		// 2 days for adult-calf horizontal transmission
							if (dp < infadultcalf) {
								diseasestage[i] = 5;	// infected
							}
						} else {			// this is calf rearing loop
							if (dp < infcalfcalf) {
								diseasestage[i] = 5;
							}
						}
					}
				}



				//Fig 4 including calving
				//Fig 2 lactation
				// determining lactation 0
				lactationstage[i]=0;
				if (cows[i] <= 720) {		// lactation 0
					if (cows[i] <= 60) {  	// calf rearing
						// do nothing
					} else {		// heifer rearing
						if (451 == cows[i])
							lactationstage[i]=INST;
						if (480 == cows[i])
							lactationstage[i]=PREGNANT;
						if (720 == cows[i])
							lactationstage[i]=CALVING;
					}
				} else {			// lactation N > 0


					// parity 1
					if (721 == cows[i])
						lactationstage[i] = VWP;
					if (780 == cows[i])
						lactationstage[i] = INST;
					if (810 == cows[i])
						lactationstage[i] = NOTPREGNANT;
					if (831 == cows[i])
						lactationstage[i] = INST;
					if (861 == cows[i])
						lactationstage[i] = PREGNANT;
					if (1000 == cows[i])
						lactationstage[i] = DP;
					if (1060 == cows[i])
						lactationstage[i] = CALVING;

					// parity 2
					if (721+340 == cows[i])
						lactationstage[i] = VWP;
					if (780+340 == cows[i])
						lactationstage[i] = INST;
					if (810+340 == cows[i])
						lactationstage[i] = NOTPREGNANT;
					if (831+340 == cows[i])
						lactationstage[i] = INST;
					if (861+340 == cows[i])
						lactationstage[i] = PREGNANT;
					if (1000+340 == cows[i])
						lactationstage[i] = DP;
					if (1060+340 == cows[i])
						lactationstage[i] = CALVING;

					// parity 3
					if (721+340+340 == cows[i])
						lactationstage[i] = VWP;
					if (780+340+340 == cows[i])
						lactationstage[i] = INST;
					if (810+340+340 == cows[i])
						lactationstage[i] = NOTPREGNANT;
					if (831+340+340 == cows[i])
						lactationstage[i] = INST;
					if (861+340+340 == cows[i])
						lactationstage[i] = PREGNANT;
					if (1000+340+340 == cows[i])
						lactationstage[i] = DP;
					if (1060+340+340 == cows[i])
						lactationstage[i] = CALVING;

					// parity 4
					if (721+340+340+340 == cows[i])
						lactationstage[i] = VWP;
					if (780+340+340+340 == cows[i])
						lactationstage[i] = INST;
					if (810+340+340+340 == cows[i])
						lactationstage[i] = NOTPREGNANT;
					if (831+340+340+340 == cows[i])
						lactationstage[i] = INST;
					if (861+340+340+340 == cows[i])
						lactationstage[i] = PREGNANT;
					if (1000+340+340+340 == cows[i])
						lactationstage[i] = DP;
					if (1060+340+340+340 == cows[i])
						lactationstage[i] = CALVING;

					// parity 5
					if (721+340+340+340+340 == cows[i])
						lactationstage[i] = VWP;
					if (780+340+340+340+340 == cows[i])
						lactationstage[i] = INST;
					if (810+340+340+340+340 == cows[i])
						lactationstage[i] = NOTPREGNANT;
					if (831+340+340+340+340 == cows[i])
						lactationstage[i] = INST;
					if (861+340+340+340+340 == cows[i])
						lactationstage[i] = PREGNANT;
					if (1000+340+340+340+340 == cows[i])
						lactationstage[i] = DP;
					if (1060+340+340+340+340 == cows[i])
						lactationstage[i] = CALVING;

					// parity 6
					if (721+340+340+340+340+340 == cows[i])
						lactationstage[i] = VWP;
					if (780+340+340+340+340+340 == cows[i])
						lactationstage[i] = INST;
					if (810+340+340+340+340+340 == cows[i])
						lactationstage[i] = NOTPREGNANT;
					if (831+340+340+340+340+340 == cows[i])
						lactationstage[i] = INST;
					if (861+340+340+340+340+340 == cows[i])
						lactationstage[i] = PREGNANT;
					if (1000+340+340+340+340+340 == cows[i])
						lactationstage[i] = DP;
					if (1060+340+340+340+340+340 == cows[i])
						lactationstage[i] = CALVING;

					// parity 7
					if (721+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = VWP;
					if (780+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = INST;
					if (810+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = NOTPREGNANT;
					if (831+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = INST;
					if (861+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = PREGNANT;
					if (1000+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = DP;
					if (1060+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = CALVING;

					// parity 8
					if (721+340+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = VWP;
					if (780+340+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = INST;
					if (810+340+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = NOTPREGNANT;
					if (831+340+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = INST;
					if (861+340+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = PREGNANT;
					if (1000+340+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = DP;
					if (1060+340+340+340+340+340+340+340 == cows[i])
						lactationstage[i] = CALVING;


				}

				if (CALVING == lactationstage[i]) {
					// calculate the probability of a female calf being born
					double fp;
					fp = Math.random();
					// 0.70 / 365 pregnancy probability
					if (fp < 0.5 * 0.70) {		// female
						cows[nextnumcows] = 0;		// age of calf
						compartment[nextnumcows] = 0;	// calf
						parity[nextnumcows] = 0;
						livingstatus[nextnumcows]=1;

						double pv;
						pv = Math.random();
						if (0 == diseasestage[i])	// susceptible dam
							diseasestage[nextnumcows] = 0;	// susceptible calf
						else if (1 == diseasestage[i]) {  // latent dam
							if (pv < vh)
								diseasestage[nextnumcows] = 5; 	// infected calf, prob vertical dam to calf
						} else if (2 == diseasestage[i]) { // low shedding dam
							if (pv < vy1)
								diseasestage[nextnumcows] = 5;  // infected calf
						} else if (3 == diseasestage[i]) { // high shedding dam
							if (pv < vy2)
								diseasestage[nextnumcows] = 5;
						}

						lactationstage[nextnumcows] = 0;	// calf
						nextnumcows++;

					}
				}

				if (nextnumcows > numcows) {
					numcows = nextnumcows;
					tncows = tncows+1;
					//System.out.println("new #cows = " + tncows);
				}

				if (10==t)
				{
					System.out.print(cows[i]);
				//System.out.print(diseasestage[i]);
				//System.out.print(compartment[i]);
					System.out.print(" ");
					System.out.print(lactationstage[i]);
					System.out.print(",");
				}
			}
			System.out.println();

			for (int x=0; x<numcows; x++) {
				// advance the age of each cow by one day
				if (0==livingstatus[x])		// skip dead cows
					continue;

				cows[x]++;

				double td;
				if (0==compartment[x])
				{
					td=Math.random();
					if (td < 0.000167) { // calf
						livingstatus[x]=0;
						tncows=tncows-1;
					}
				} else if (1==compartment[x]) { //heifer
					td=Math.random();
					if (td <0.0000278) {
						livingstatus[x]=0;
						tncows=tncows-1;
					}
				} else { //adult
					td=Math.random();
					if (td<0.00055) {
						livingstatus[x]=0;
						tncows=tncows-1;
					}
				}


				/*
				if (cows[x] > 720+8*340) {
					livingstatus[x]=0;	// died after parity 8
					tncows=tncows-1;


				}
				*/

			}

			// annual culling 30-40%
			double randomD = Math.random();

			if (randomD < 0.39) {	// cull with 30-40% annual prob
				int randcow;

				// check if at least 1 cow is alive
				int ysum=0;
				for (int y=0; y<numcows; y++)
					ysum=ysum+livingstatus[y];

				if (ysum>0)
				{
					randcow = (int) Math.ceil(Math.random() * numcows);
					while (0==livingstatus[randcow])	// find a living cow
						randcow = (int) Math.ceil(Math.random() * numcows);

					livingstatus[randcow] = 0;	// make the cow randcow dead
					tncows = tncows-1;
					//System.out.println("cull #cow = " + tncows);
				}
				else
				{
					System.out.println("ERROR! all cows dead! cannot cull dead cows");
				}
			}


		}

	}
}
