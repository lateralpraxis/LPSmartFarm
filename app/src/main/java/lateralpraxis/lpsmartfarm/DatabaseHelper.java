package lateralpraxis.lpsmartfarm;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by LPNOIDA01 on 9/26/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private DatabaseAdapter databaseAdapter;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    public DatabaseHelper(Context context) {

        super(context, DatabaseAdapter.DATABASE_NAME, null,
                DatabaseAdapter.DATABASE_VERSION);
        databaseAdapter = new DatabaseAdapter(context);
        // TODO Auto-generated constructor stub
    }


    @Override
    public void onCreate(SQLiteDatabase _db) {
        _db.execSQL(DatabaseAdapter.ExceptionTABLE_CREATE);
        _db.execSQL(DatabaseAdapter.UserRoles_CREATE);
        _db.execSQL(DatabaseAdapter.State_CREATE);
        _db.execSQL(DatabaseAdapter.District_CREATE);
        _db.execSQL(DatabaseAdapter.City_CREATE);
        _db.execSQL(DatabaseAdapter.Block_CREATE);
        _db.execSQL(DatabaseAdapter.Panchayat_CREATE);
        _db.execSQL(DatabaseAdapter.Vilage_CREATE);
        _db.execSQL(DatabaseAdapter.PinCode_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerType_CREATE);
        _db.execSQL(DatabaseAdapter.EducationLevel_CREATE);
        _db.execSQL(DatabaseAdapter.Farmer_CREATE);
        _db.execSQL(DatabaseAdapter.Address_CREATE);
        _db.execSQL(DatabaseAdapter.AddressData_CREATE);
        _db.execSQL(DatabaseAdapter.Organizer_CREATE);
        _db.execSQL(DatabaseAdapter.LandIssue_CREATE);
        _db.execSQL(DatabaseAdapter.SoilType_CREATE);
        _db.execSQL(DatabaseAdapter.LandType_CREATE);
        _db.execSQL(DatabaseAdapter.ExistingUse_CREATE);
        _db.execSQL(DatabaseAdapter.CommunityUse_CREATE);
        _db.execSQL(DatabaseAdapter.ExistingHazard_CREATE);
        _db.execSQL(DatabaseAdapter.NearestDam_CREATE);
        _db.execSQL(DatabaseAdapter.NearestRiver_CREATE);
        _db.execSQL(DatabaseAdapter.ElectricitySource_CREATE);
        _db.execSQL(DatabaseAdapter.WaterSource_CREATE);
        _db.execSQL(DatabaseAdapter.IrrigationSystem_CREATE);
        _db.execSQL(DatabaseAdapter.LandCharacteristic_CREATE);
        _db.execSQL(DatabaseAdapter.Season_CREATE);
        _db.execSQL(DatabaseAdapter.Crop_CREATE);
        _db.execSQL(DatabaseAdapter.Variety_CREATE);
        _db.execSQL(DatabaseAdapter.ProofType_CREATE);
        _db.execSQL(DatabaseAdapter.POAPOI_CREATE);
        _db.execSQL(DatabaseAdapter.TempFile_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerDoc_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerProofTemp_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerProof_CREATE);
        _db.execSQL(DatabaseAdapter.Languages_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerTemp_CREATE);
        _db.execSQL(DatabaseAdapter.AddressTemp_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerPlantation_CREATE);
        _db.execSQL(DatabaseAdapter.InterCropping_CREATE);
        _db.execSQL(DatabaseAdapter.LegalDispute_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerOperatingBlocksTemp_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerOperatingBlocks_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerOtherDetailsTemp_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerOtherDetails_CREATE);
        _db.execSQL(DatabaseAdapter.LoanSource_CREATE);
        _db.execSQL(DatabaseAdapter.LoanType_CREATE);
        _db.execSQL(DatabaseAdapter.RelationShip_CREATE);
        _db.execSQL(DatabaseAdapter.FarmBlock_CREATE);
        _db.execSQL(DatabaseAdapter.TempFarmerDocumentFile_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerFamilyMember_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerFamilyMemberTemp_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerLoanDetailsTemp_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerLoanDetails_CREATE);
        _db.execSQL(DatabaseAdapter.FarmAsset_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerAssetDetails_CREATE);
        _db.execSQL(DatabaseAdapter.FarmBlockCroppingPattern_CREATE);
        _db.execSQL(DatabaseAdapter.Monthage_CREATE);
        _db.execSQL(DatabaseAdapter.FarmBlockLandCharacteristic_CREATE);
        _db.execSQL(DatabaseAdapter.FarmBlockLandIssue_CREATE);
        _db.execSQL(DatabaseAdapter.TEMPGPS_CREATE);
        _db.execSQL(DatabaseAdapter.FarmBlockCoordinates_CREATE);
        _db.execSQL(DatabaseAdapter.FarmerSyncTable_CREATE);
        _db.execSQL(DatabaseAdapter.FarmBlockSyncTable_CREATE);
        _db.execSQL(DatabaseAdapter.PlantationSyncTable_CREATE);
        _db.execSQL(DatabaseAdapter.PlantType_CREATE);
        _db.execSQL(DatabaseAdapter.PlantingSystem_CREATE);
        _db.execSQL(DatabaseAdapter.InterCroppingSyncTable_CREATE);
        _db.execSQL(DatabaseAdapter.Nursery_CREATE);
        _db.execSQL(DatabaseAdapter.NurseryZone_CREATE);
        _db.execSQL(DatabaseAdapter.NurseryAccountDetail_CREATE);
        _db.execSQL(DatabaseAdapter.NurseryCroppingPattern_CREATE);
        _db.execSQL(DatabaseAdapter.NurseryLandCharacteristic_CREATE);
        _db.execSQL(DatabaseAdapter.NurseryLandIssue_CREATE);
        _db.execSQL(DatabaseAdapter.TEMPNURSERYGPS_CREATE);
        _db.execSQL(DatabaseAdapter.NurseryCoordinates_CREATE);
        _db.execSQL(DatabaseAdapter.NurseryPlantation_CREATE);
        _db.execSQL(DatabaseAdapter.NurseryPlantationSyncTable_CREATE);
        _db.execSQL(DatabaseAdapter.NurseryInterCropping_CREATE);
        _db.execSQL(DatabaseAdapter.NurseryInterCroppingSyncTable_CREATE);
        _db.execSQL(DatabaseAdapter.UOM_CREATE);
        _db.execSQL(DatabaseAdapter.FarmActivityType_CREATE);
        _db.execSQL(DatabaseAdapter.FarmActivity_CREATE);
        _db.execSQL(DatabaseAdapter.FarmSubActivity_CREATE);
        _db.execSQL(DatabaseAdapter.PlannedActivity_CREATE);
        _db.execSQL(DatabaseAdapter.AllActivity_CREATE);
        _db.execSQL(DatabaseAdapter.RecommendedActivity_CREATE);
        _db.execSQL(DatabaseAdapter.JobCardDetailTemp_CREATE);
        _db.execSQL(DatabaseAdapter.JobCard_CREATE);
        _db.execSQL(DatabaseAdapter.JobCardDetail_CREATE);
        _db.execSQL(DatabaseAdapter.JobCardPending_CREATE);
        _db.execSQL(DatabaseAdapter.VisitReport_CREATE);
        _db.execSQL(DatabaseAdapter.VisitReportDetail_CREATE);
        _db.execSQL(DatabaseAdapter.VisitReportPhoto_CREATE);
        _db.execSQL(DatabaseAdapter.PlantStatus_CREATE);
        _db.execSQL(DatabaseAdapter.Defect_CREATE);
        _db.execSQL(DatabaseAdapter.TempJobCardFile_CREATE);
        _db.execSQL(DatabaseAdapter.PlantationWeek_CREATE);
        _db.execSQL(DatabaseAdapter.BookingAddressTemp_CREATE);
        _db.execSQL(DatabaseAdapter.Recommendation_CREATE);
        _db.execSQL(DatabaseAdapter.RecommendationDetail_CREATE);
        _db.execSQL(DatabaseAdapter.PaymentMode_CREATE);
        _db.execSQL(DatabaseAdapter.PolyBagRate_CREATE);
        _db.execSQL(DatabaseAdapter.Booking_CREATE);
        _db.execSQL(DatabaseAdapter.ShortCloseReason_CREATE);
        _db.execSQL(DatabaseAdapter.PendingDispatchForDelivery_CREATE);
        _db.execSQL(DatabaseAdapter.PendingDispatchDetailsForDelivery_CREATE);
        _db.execSQL(DatabaseAdapter.DeliveryDetailsForDispatch_CREATE);
        _db.execSQL(DatabaseAdapter.BalanceDetailsForFarmerNursery_CREATE);
        _db.execSQL(DatabaseAdapter.PaymentAgainstDispatchDelivery_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
        onCreate(_db);
    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {

            databaseAdapter.insertExceptions(sqlEx.getMessage(), "DatabaseHelper.java", "getData");
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {


            databaseAdapter.insertExceptions(ex.getMessage(), "DatabaseHelper.java", "getData");
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }


    }

}
