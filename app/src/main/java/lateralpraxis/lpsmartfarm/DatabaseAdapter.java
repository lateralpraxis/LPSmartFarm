package lateralpraxis.lpsmartfarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import lateralpraxis.type.AttachmentDetails;
import lateralpraxis.type.CustomCoord;
import lateralpraxis.type.CustomData;
import lateralpraxis.type.CustomType;
import lateralpraxis.type.FamilyMember;
import lateralpraxis.type.FarmBlockCroppingPatternData;
import lateralpraxis.type.FarmBlockData;
import lateralpraxis.type.FarmBlockViewData;
import lateralpraxis.type.FarmerAssetData;
import lateralpraxis.type.FarmerData;
import lateralpraxis.type.FarmerOtherData;
import lateralpraxis.type.InterCroppingData;
import lateralpraxis.type.JobCardConfirmationData;
import lateralpraxis.type.LandCharacteristic;
import lateralpraxis.type.LoanDetails;
import lateralpraxis.type.NurseryCroppingPatternData;
import lateralpraxis.type.NurseryData;
import lateralpraxis.type.NurseryInterCroppingData;
import lateralpraxis.type.NurseryPlantationData;
import lateralpraxis.type.NurseryViewData;
import lateralpraxis.type.NurseryZoneData;
import lateralpraxis.type.ObservationData;
import lateralpraxis.type.OperationalBlocks;
import lateralpraxis.type.PendingJobCardData;
import lateralpraxis.type.PendingNurseryJobCardData;
import lateralpraxis.type.PlantationData;
import lateralpraxis.type.ProofType;
import lateralpraxis.type.VisitData;
import lateralpraxis.type.VisitNurseryData;
import lateralpraxis.type.VisitNurseryViewData;
import lateralpraxis.type.VisitViewData;

/**
 * Created by LPNOIDA01 on 9/26/2017.
 */

public class DatabaseAdapter {

    public static final int NAME_COLUMN = 1;
    static final String DATABASE_NAME = "lpfarmart.db";
    static final int DATABASE_VERSION = 777301;
    /*Code to create tables*/
    static final String
            ExceptionTABLE_CREATE =
            "CREATE TABLE IF NOT EXISTS Exceptions(Id INTEGER PRIMARY KEY AUTOINCREMENT, Message TEXT, ActivityName TEXT, CalledMethod TEXT, PhoneInfo TEXT, CreatedOn DATETIME);",
            UserRoles_CREATE =
                    "CREATE TABLE IF NOT EXISTS UserRoles(UserId TEXT, UserName TEXT, RoleName TEXT,StateId TEXT,DistrictId TEXT,BlockId TEXT,NurseryId TEXT);",
            State_CREATE =
                    "CREATE TABLE IF NOT EXISTS State(StateId TEXT, StateName TEXT, ShortName TEXT, StateCode TEXT, StateNameLocal TEXT);",
            District_CREATE =
                    "CREATE TABLE IF NOT EXISTS District(DistrictId TEXT, StateId TEXT, DistrictName TEXT,DistrictNameLocal TEXT);",
            City_CREATE =
                    "CREATE TABLE IF NOT EXISTS City(CityId TEXT, DistrictId TEXT, CityName TEXT, CityNameLocal TEXT);",
            Block_CREATE =
                    "CREATE TABLE IF NOT EXISTS Block(BlockId TEXT, DistrictId TEXT, BlockName TEXT, BlockNameLocal TEXT);",
            Panchayat_CREATE =
                    "CREATE TABLE IF NOT EXISTS Panchayat(PanchayatId TEXT, BlockId TEXT, PanchayatName TEXT, PanchayatNameLocal TEXT);",
            Vilage_CREATE =
                    "CREATE TABLE IF NOT EXISTS Village(VillageId TEXT, PanchayatId TEXT, VillageName TEXT, VillageNameLocal TEXT);",
            PinCode_CREATE =
                    "CREATE TABLE IF NOT EXISTS PinCode(PinCodeId TEXT, StateId TEXT, DistrictId TEXT, CityId TEXT" +
                            ", BlockId TEXT, PanchayatId TEXT, VillageId TEXT, PinCode TEXT);",
            FarmerType_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerType(FarmerTypeId TEXT, FarmerType TEXT, FarmerTypeLocal TEXT);",
            Farmer_CREATE =
                    "CREATE TABLE IF NOT EXISTS Farmer(Id INTEGER PRIMARY KEY AUTOINCREMENT,FarmerUniqueId TEXT, FarmerCode TEXT, " +
                            "EducationLevelId TEXT, FarmerTypeId TEXT, Salutation TEXT, FirstName TEXT, MiddleName TEXT, LastName TEXT," +
                            " FatherSalutation TEXT,FatherFirstName TEXT,FatherMiddleName TEXT,FatherLastName TEXT," +
                            "EmailId TEXT,Mobile TEXT,Mobile1 TEXT,Mobile2 TEXT,BirthDate TEXT,Gender TEXT,BankAccountNo TEXT," +
                            "IFSCCode TEXT,TotalAcreage TEXT,FSSAINumber TEXT,RegistrationDate TEXT,ExpiryDate TEXT,LanguageId TEXT, CreateBy TEXT, CreateDate TEXT,Longitude TEXT, Latitude TEXT,Accuracy TEXT, IsSync TEXT, IsDuplicate TEXT, IsLoanNotApplicable TEXT, SalutationLocal TEXT,FirstNameLocal TEXT,MiddleNameLocal TEXT,LastNameLocal TEXT,FatherSalutationLocal TEXT,FatherFirstNameLocal TEXT,FatherMiddleNameLocal TEXT,FatherLastNameLocal TEXT)",
            FarmerTemp_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerTemp(Id INTEGER PRIMARY KEY AUTOINCREMENT,FarmerUniqueId TEXT, " +
                            "EducationLevelId TEXT, FarmerTypeId TEXT, Salutation TEXT, FirstName TEXT, MiddleName TEXT, LastName TEXT," +
                            " FatherSalutation TEXT,FatherFirstName TEXT,FatherMiddleName TEXT,FatherLastName TEXT," +
                            "EmailId TEXT,Mobile TEXT,Mobile1 TEXT,Mobile2 TEXT,BirthDate TEXT,Gender TEXT,BankAccountNo TEXT," +
                            "IFSCCode TEXT,TotalAcreage TEXT,FSSAINumber TEXT,RegistrationDate TEXT,ExpiryDate TEXT,LanguageId TEXT, CreateBy TEXT, CreateDate TEXT,Longitude TEXT, Latitude TEXT,Accuracy TEXT, IsSync TEXT)",
            Address_CREATE =
                    "CREATE TABLE IF NOT EXISTS Address(FarmerUniqueId TEXT,Street1 TEXT, Street2 TEXT" +
                            ", StateId TEXT, DistrictId TEXT, BlockId TEXT, PanchayatId TEXT, VillageId TEXT" +
                            ", CityId TEXT, PinCodeId TEXT, AddressType TEXT)",
            AddressData_CREATE =
                    "CREATE TABLE IF NOT EXISTS AddressData(UniqueId TEXT,Street1 TEXT, Street2 TEXT" +
                            ", StateId TEXT, DistrictId TEXT, BlockId TEXT, PanchayatId TEXT, VillageId TEXT" +
                            ", CityId TEXT, PinCodeId TEXT, AddressType TEXT)",
            FarmerPlantation_CREATE = "CREATE TABLE IF NOT EXISTS FarmerPlantation (Id INTEGER PRIMARY KEY AUTOINCREMENT, PlantationUniqueId TEXT,PlantationCode TEXT, FarmerUniqueId TEXT, " +
                    "FarmBlockUniqueId TEXT, ZoneId, CropId TEXT, CropVarietyId TEXT, PlantTypeId TEXT, MonthAgeId TEXT, Acreage TEXT, PlantationDate TEXT, PlantingSystemId TEXT, " +
                    "PlantRow TEXT, PlantColumn TEXT, Balance TEXT, TotalPlant, CreateBy TEXT, CreateDate TEXT, Longitude TEXT, Latitude TEXT,Accuracy TEXT,IsSync TEXT)",
            InterCropping_CREATE = "CREATE TABLE IF NOT EXISTS InterCropping (Id INTEGER PRIMARY KEY AUTOINCREMENT, InterCroppingUniqueId TEXT, FarmerUniqueId TEXT, " +
                    "FarmBlockUniqueId TEXT, FarmerPlantationUniqueId TEXT, CropVarietyId TEXT, Acreage TEXT, SeasonId TEXT, CreateBy TEXT, CreateDate TEXT, Longitude TEXT, Latitude TEXT,Accuracy TEXT,IsSync TEXT, FinancialYear TEXT)",
            AddressTemp_CREATE = "CREATE TABLE IF NOT EXISTS AddressTemp(FarmerUniqueId TEXT,Street1 TEXT, Street2 TEXT" +
                    ", StateId TEXT, DistrictId TEXT, BlockId TEXT, PanchayatId TEXT, VillageId TEXT" +
                    ", CityId TEXT, PinCodeId TEXT, AddressType TEXT)",
            EducationLevel_CREATE =
                    "CREATE TABLE IF NOT EXISTS EducationLevel(EducationLevelId TEXT, EducationLevel TEXT, EducationLevelLocal TEXT);",
            ProofType_CREATE =
                    "CREATE TABLE IF NOT EXISTS ProofType(Id TEXT, Name TEXT, NameLocal TEXT);",
            POAPOI_CREATE =
                    "CREATE TABLE IF NOT EXISTS POAPOI(Id TEXT, ProofTypeId TEXT,Name TEXT, NameLocal TEXT);",
            Organizer_CREATE =
                    "CREATE TABLE IF NOT EXISTS Organizer(Id TEXT, Title TEXT, TitleHindi TEXT);",
            LandIssue_CREATE =
                    "CREATE TABLE IF NOT EXISTS LandIssue(Id TEXT, Title TEXT, TitleHindi TEXT);",
            SoilType_CREATE =
                    "CREATE TABLE IF NOT EXISTS SoilType(Id TEXT, Title TEXT, TitleHindi TEXT);",
            LandType_CREATE =
                    "CREATE TABLE IF NOT EXISTS LandType(Id TEXT, Title TEXT, TitleHindi TEXT);",
            ExistingUse_CREATE =
                    "CREATE TABLE IF NOT EXISTS ExistingUse(Id TEXT, Title TEXT, TitleHindi TEXT);",
            CommunityUse_CREATE =
                    "CREATE TABLE IF NOT EXISTS CommunityUse(Id TEXT, Title TEXT, TitleHindi TEXT);",
            ExistingHazard_CREATE =
                    "CREATE TABLE IF NOT EXISTS ExistingHazard(Id TEXT, Title TEXT, TitleHindi TEXT);",
            NearestDam_CREATE =
                    "CREATE TABLE IF NOT EXISTS NearestDam(Id TEXT, Title TEXT, TitleHindi TEXT);",
            NearestRiver_CREATE =
                    "CREATE TABLE IF NOT EXISTS NearestRiver(Id TEXT, Title TEXT, TitleHindi TEXT);",
            ElectricitySource_CREATE =
                    "CREATE TABLE IF NOT EXISTS ElectricitySource(Id TEXT, Title TEXT, TitleHindi TEXT);",
            WaterSource_CREATE =
                    "CREATE TABLE IF NOT EXISTS WaterSource(Id TEXT, Title TEXT, TitleHindi TEXT);",
            LoanSource_CREATE =
                    "CREATE TABLE IF NOT EXISTS LoanSource(Id TEXT, Title TEXT, TitleHindi TEXT);",
            LoanType_CREATE =
                    "CREATE TABLE IF NOT EXISTS LoanType(Id TEXT, Title TEXT, TitleHindi TEXT);",
            FarmAsset_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmAsset(Id TEXT, Title TEXT, TitleHindi TEXT);",
            RelationShip_CREATE =
                    "CREATE TABLE IF NOT EXISTS RelationShip(Id TEXT, Title TEXT, TitleHindi TEXT);",
            IrrigationSystem_CREATE =
                    "CREATE TABLE IF NOT EXISTS IrrigationSystem(Id TEXT, Title TEXT, TitleHindi TEXT);",
            LandCharacteristic_CREATE =
                    "CREATE TABLE IF NOT EXISTS LandCharacteristic(Id TEXT, Title TEXT, TitleHindi TEXT);",
            Season_CREATE =
                    "CREATE TABLE IF NOT EXISTS Season(Id TEXT, Title TEXT, TitleHindi TEXT);",
            Crop_CREATE =
                    "CREATE TABLE IF NOT EXISTS Crop(Id TEXT, Title TEXT, TitleHindi TEXT, IsPlantation TEXT);",
            LegalDispute_CREATE =
                    "CREATE TABLE IF NOT EXISTS LegalDispute(Id TEXT, Title TEXT, TitleHindi TEXT);",
            Variety_CREATE =
                    "CREATE TABLE IF NOT EXISTS Variety(Id TEXT, CropId TEXT, Title TEXT, TitleHindi TEXT);",
            Monthage_CREATE =
                    "CREATE TABLE IF NOT EXISTS Monthage(Id TEXT, Title TEXT, TitleHindi TEXT);",
            TempFile_CREATE =
                    "CREATE TABLE IF NOT EXISTS TempFile(Type TEXT, FileName TEXT, IsSync TEXT)",
            TempJobCardFile_CREATE =
                    "CREATE TABLE IF NOT EXISTS TempJobCardFile(ActivityId TEXT,SubActivityId TEXT,Type TEXT, FileName TEXT, IsSync TEXT)",
            TempFarmerDocumentFile_CREATE =
                    "CREATE TABLE IF NOT EXISTS TempFarmerDocument(Id INTEGER PRIMARY KEY AUTOINCREMENT,FarmerUniqueId TEXT,Type TEXT, FileName TEXT)",
            FarmerDoc_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerDocuments(Id INTEGER PRIMARY KEY AUTOINCREMENT,FarmerUniqueId TEXT,Type TEXT, FileName TEXT, IsSync TEXT)",
            FarmerProofTemp_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerProofTemp(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmerUniqueId TEXT,UniqueId TEXT, POAPOIId TEXT, DocumentNo TEXT, FileName TEXT, FilePath TEXT, IsSync TEXT)",
            FarmerProof_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerProof(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmerUniqueId TEXT, UniqueId TEXT,POAPOIId TEXT, DocumentNo TEXT, FileName TEXT, FilePath TEXT, CreateBy TEXT, CreateDate TEXT, IsSync TEXT)",
            Languages_CREATE =
                    "CREATE TABLE IF NOT EXISTS Languages(Id INTEGER,Name TEXT, NameLocal TEXT,Culture TEXT)",
            FarmerOperatingBlocksTemp_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerOperatingBlocksTemp(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmerUniqueId TEXT,DistrictId TEXT, BlockId TEXT)",
            FarmerOperatingBlocks_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerOperatingBlocks(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmerUniqueId TEXT,DistrictId TEXT, BlockId TEXT)",
            FarmerOtherDetailsTemp_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerOtherDetailsTemp(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmerUniqueId TEXT,SoilTypeId TEXT, IrrigationSystemId TEXT, RiverId TEXT, DamId TEXT, WaterSourceId TEXT, ElectricitySourceId TEXT)",
            FarmerOtherDetails_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerOtherDetails(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmerUniqueId TEXT,SoilTypeId TEXT, IrrigationSystemId TEXT, RiverId TEXT, DamId TEXT, WaterSourceId TEXT, ElectricitySourceId TEXT)",
            FarmBlock_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmBlock(Id INTEGER PRIMARY KEY  AUTOINCREMENT, " +
                            "FarmBlockUniqueId TEXT, FarmBlockCode TEXT, FarmerId TEXT, LandTypeId TEXT, FPOId TEXT, AddressId TEXT, KhataNo TEXT, KhasraNo TEXT, " +
                            "ContractDate TEXT, Acerage TEXT, SoilTypeId TEXT, ElevationMSL TEXT, PHChemical TEXT, Nitrogen TEXT, Potash TEXT," +
                            "Phosphorus TEXT, OrganicCarbonPerc TEXT, Magnesium TEXT, Calcium TEXT, ExistingUseId TEXT, CommunityUseId TEXT," +
                            "ExistingHazardId TEXT, RiverId TEXT, DamId TEXT, IrrigationId TEXT, OverheadTransmission TEXT, LegalDisputeId TEXT," +
                            "SourceWaterId TEXT, ElectricitySourceId TEXT, DripperSpacing TEXT, DischargeRate TEXT, ReasonForExitId TEXT," +
                            "ExitRemarks TEXT, CreateBy TEXT, CreateDate TEXT,Longitude TEXT, Latitude TEXT,Accuracy TEXT, IsSync TEXT, OwnerName TEXT, OwnerMobile TEXT, JobCardAllowed TEXT)",
            FarmerFamilyMemberTemp_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerFamilyMemberTemp(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmerUniqueId TEXT, MemberName TEXT,Gender TEXT,BirthDate TEXT,RelationshipId TEXT, IsNominee TEXT, NomineePercentage TEXT, CreateDate TEXT, CreateBy TEXT, IsSync TEXT)",
            FarmerFamilyMember_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerFamilyMember(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmerUniqueId TEXT, MemberName TEXT,Gender TEXT,BirthDate TEXT,RelationshipId TEXT, IsNominee TEXT, NomineePercentage TEXT, CreateDate TEXT, CreateBy TEXT, IsSync TEXT, MemberNameLocal TEXT)",
            FarmerLoanDetailsTemp_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerLoanDetailsTemp(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmerUniqueId TEXT, LoanSourceId TEXT, LoanTypeId TEXT, ROIPercentage TEXT, LoanAmount TEXT, BalanceAmount TEXT, Tenure TEXT, CreateBy TEXT, CreateDate TEXT, IsSync TEXT)",
            FarmerLoanDetails_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerLoanDetails(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmerUniqueId TEXT, LoanSourceId TEXT, LoanTypeId TEXT, ROIPercentage TEXT, LoanAmount TEXT, BalanceAmount TEXT, Tenure TEXT, CreateBy TEXT, CreateDate TEXT, IsSync TEXT)",
            FarmerAssetDetails_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmerAssetDetails(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmerUniqueId TEXT, FarmAssetsId TEXT, FarmAssetsNo TEXT, CreateBy TEXT, CreateDate TEXT, IsSync TEXT)",
            FarmBlockCroppingPattern_CREATE = "CREATE TABLE IF NOT EXISTS FarmBlockCroppingPattern(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmBlockUniqueId TEXT,CropId TEXT,CropVarietyId TEXT, SeasonId TEXT, Acreage TEXT)",
            FarmBlockLandCharacteristic_CREATE = "CREATE TABLE IF NOT EXISTS FarmBlockLandCharacteristic(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmBlockUniqueId TEXT,LandCharacteristicId TEXT)",
            FarmBlockLandIssue_CREATE = "CREATE TABLE IF NOT EXISTS FarmBlockLandIssue(Id INTEGER PRIMARY KEY  AUTOINCREMENT,FarmBlockUniqueId TEXT,LandIssueId TEXT)",
            TEMPGPS_CREATE = "CREATE TABLE IF NOT EXISTS TempGPS(Id INTEGER PRIMARY KEY,Latitude TEXT, Longitude TEXT,Accuracy TEXT);",
            FarmBlockCoordinates_CREATE = "CREATE TABLE IF NOT EXISTS FarmBlockCoordinates(Id INTEGER PRIMARY KEY,FarmBlockUniqueId TEXT, Latitude TEXT, Longitude TEXT,Accuracy TEXT);",
            FarmerSyncTable_CREATE = "CREATE TABLE IF NOT EXISTS FarmerSyncTable(FarmerUniqueId TEXT);",
            FarmBlockSyncTable_CREATE = "CREATE TABLE IF NOT EXISTS FarmBlockSyncTable(FarmBlockUniqueId TEXT);",
            PlantationSyncTable_CREATE = "CREATE TABLE IF NOT EXISTS PlantationSyncTable(PlantationUniqueId TEXT);",
            InterCroppingSyncTable_CREATE = "CREATE TABLE IF NOT EXISTS InterCroppingSyncTable(InterCroppingUniqueId TEXT);",
            PlantType_CREATE =
                    "CREATE TABLE IF NOT EXISTS PlantType(Id TEXT, Title TEXT, TitleHindi TEXT);",
            PlantingSystem_CREATE =
                    "CREATE TABLE IF NOT EXISTS PlantingSystem(Id TEXT, Title TEXT, TitleHindi TEXT);",
            Nursery_CREATE = "CREATE TABLE IF NOT EXISTS Nursery(Id TEXT, UniqueId TEXT,  Title TEXT, NurseryType TEXT, NurseryTypeHindi TEXT, TitleHindi TEXT, Salutation TEXT, FirstName TEXT, MiddleName TEXT, LastName TEXT, Mobile TEXT, AlternateMobile TEXT, EmailId TEXT, LandType TEXT, AddressUniqueId TEXT, RegistryDate TEXT, OfficePrimise TEXT, GSTNo TEXT, GSTDate TEXT, OwnerName TEXT, LoginId TEXT, ContactNo TEXT, MainNurseryId TEXT, CertifiedBy TEXT, RegistrationNo TEXT, RegistrationDate TEXT, KhataNo TEXT, KhasraNo TEXT, ContractDate TEXT, Area TEXT, SoilTypeId TEXT, ElevationMSL TEXT, SoilPH TEXT, Nitrogen TEXT, Potash TEXT, Phosphorus TEXT, OrganicCarbonPerc TEXT, Magnesium TEXT, Calcium TEXT, ExistingUseId TEXT, CommunityUseId TEXT, ExistingHazardId TEXT, RiverId TEXT, DamId TEXT, IrrigationId TEXT, OverheadTransmission TEXT, LegalDisputeId TEXT, SourceWaterId TEXT, ElectricitySourceId TEXT, DripperSpacing TEXT, DischargeRate TEXT);",
            NurseryAccountDetail_CREATE = "CREATE TABLE IF NOT EXISTS NurseryAccountDetail(Id TEXT, NurseryId TEXT, AccountNo TEXT, IFSCCode TEXT)",
            NurseryCroppingPattern_CREATE = "CREATE TABLE IF NOT EXISTS NurseryCroppingPattern(Id TEXT, NurseryId TEXT, CropVarietyId TEXT, Acreage TEXT, SeasonId TEXT, FinancialYear TEXT)",
            NurseryLandCharacteristic_CREATE = "CREATE TABLE IF NOT EXISTS NurseryLandCharacteristic(Id TEXT, NurseryId TEXT, LandCharacteristicId TEXT)",
            NurseryLandIssue_CREATE = "CREATE TABLE IF NOT EXISTS NurseryLandIssue(Id TEXT, NurseryId TEXT, LandIssueId TEXT)",
            NurseryZone_CREATE = "CREATE TABLE IF NOT EXISTS NurseryZone(Id TEXT, UniqueId TEXT, NurseryId TEXT, Title TEXT, TitleHindi TEXT, Area Text);",
            NurseryCoordinates_CREATE = "CREATE TABLE IF NOT EXISTS NurseryCoordinates(Id INTEGER PRIMARY KEY, UniqueId TEXT, NurseryId TEXT, NurseryZoneId TEXT, Latitude TEXT, Longitude TEXT, Accuracy TEXT, CreateBy TEXT, CreateDate TEXT, IsSync TEXT);",
            TEMPNURSERYGPS_CREATE = "CREATE TABLE IF NOT EXISTS TempNurseryGPS(Id INTEGER PRIMARY KEY,Latitude TEXT, Longitude TEXT,Accuracy TEXT);",
            NurseryPlantation_CREATE = "CREATE TABLE IF NOT EXISTS NurseryPlantation (Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "PlantationUniqueId TEXT, PlantationCode TEXT, NurseryUniqueId TEXT, NurseryId TEXT, ZoneId TEXT, CropId TEXT, CropVarietyId TEXT, PlantTypeId TEXT, " +
                    "MonthAgeId TEXT, Acreage TEXT, PlantationDate TEXT, PlantingSystemId TEXT, PlantRow TEXT, PlantColumn TEXT, " +
                    "Balance TEXT, TotalPlant, CreateBy TEXT, CreateDate TEXT, Longitude TEXT, Latitude TEXT,Accuracy TEXT, IsSync TEXT)",
            NurseryPlantationSyncTable_CREATE = "CREATE TABLE IF NOT EXISTS NurseryPlantationSyncTable " +
                    "(PlantationUniqueId TEXT);",
            NurseryInterCropping_CREATE = "CREATE TABLE IF NOT EXISTS NurseryInterCropping (Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "InterCroppingUniqueId TEXT, NurseryId TEXT, PlantationUniqueId TEXT, CropVarietyId TEXT, Acreage TEXT, " +
                    "SeasonId TEXT, CreateBy TEXT, CreateDate TEXT, " +
                    "Longitude TEXT, Latitude TEXT, Accuracy TEXT, IsSync TEXT, FinancialYear TEXT)",
            NurseryInterCroppingSyncTable_CREATE = "CREATE TABLE IF NOT EXISTS NurseryInterCroppingSyncTable (" +
                    "InterCroppingUniqueId TEXT);",
            UOM_CREATE =
                    "CREATE TABLE IF NOT EXISTS UOM(UomId TEXT, Title TEXT, ShortName TEXT, TitleLocal TEXT, ShortNameLocal TEXT);",
            FarmActivityType_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmActivityType(FarmActivityTypeId TEXT, Title TEXT, TitleLocal TEXT);",
            FarmActivity_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmActivity(FarmActivityId TEXT, FarmActivityTypeId TEXT, Title TEXT, TitleLocal TEXT, IsSubActivityAllowed TEXT, UomId TEXT);",
            FarmSubActivity_CREATE =
                    "CREATE TABLE IF NOT EXISTS FarmSubActivity(FarmSubActivityId TEXT, FarmActivityId TEXT, SubActivityName TEXT, SubActivityNameLocal TEXT, UomId TEXT);",
            PlannedActivity_CREATE =
                    "CREATE TABLE IF NOT EXISTS PlannedActivity(FarmBlockNurseryType TEXT,FarmBlockNurseryId TEXT,PlantationId TEXT,PlantationUniqueId TEXT,PlannerDetailId TEXT,ActivityId TEXT,SubActivityId TEXT,WeekNo TEXT,UOMId TEXT,Quantity	TEXT,Remarks TEXT,ParameterDetailId TEXT,PlantationDate  TEXT,FromDate TEXT,ToDate TEXT,FarmBlockNurseryUniqueId TEXT);",
            RecommendedActivity_CREATE =
                    "CREATE TABLE IF NOT EXISTS RecommendedActivity(FarmBlockNurseryType TEXT,FarmBlockNurseryId TEXT,PlantationId TEXT,PlantationUniqueId TEXT,ActivityId TEXT,SubActivityId TEXT,WeekNo TEXT,UOMId TEXT,Quantity TEXT,Remarks TEXT,FromDate TEXT,ToDate TEXT,FarmBlockNurseryUniqueId TEXT);",
            AllActivity_CREATE =
                    "CREATE TABLE IF NOT EXISTS AllActivity(FarmBlockNurseryType TEXT,FarmBlockNurseryId TEXT,PlantationId TEXT,PlantationUniqueId TEXT,ActivityId TEXT,SubActivityId TEXT,UomId TEXT,Remarks TEXT,FarmBlockNurseryUniqueId TEXT, WeekNo TEXT, Quantity TEXT);",
            JobCardDetailTemp_CREATE = "CREATE TABLE IF NOT EXISTS JobCardDetailTemp(Id INTEGER PRIMARY KEY AUTOINCREMENT,FarmActivityId TEXT,FarmSubActivityId TEXT, ActivityValue TEXT, ActivityType TEXT, PreviousValue TEXT, PlannerDetailId TEXT, ParameterDetailId TEXT, UniqueId TEXT, FileName TEXT);",
            JobCard_CREATE = "CREATE TABLE IF NOT EXISTS JobCard(Id INTEGER PRIMARY KEY AUTOINCREMENT,JobCardUniqueId TEXT,FarmBlockNurseryType TEXT, FarmBlockNurseryUniqueId TEXT, ZoneId TEXT, PlantationUniqueId TEXT, WeekNo TEXT, VisitDate TEXT, Latitude TEXT, Longitude TEXT, Accuracy TEXT, CreatBy TEXT, CreateDate TEXT, IsSync TEXT, CreateByRole TEXT);",
            JobCardDetail_CREATE = "CREATE TABLE IF NOT EXISTS JobCardDetail(Id INTEGER PRIMARY KEY AUTOINCREMENT,JobCardUniqueId TEXT,FarmActivityId TEXT,FarmSubActivityId TEXT,ActivityValue TEXT,ActivtyType TEXT,PlannerDetailId TEXT, ParameterDetailId TEXT, UniqueId TEXT, FileName TEXT, IsSync TEXT);",
            JobCardPending_CREATE = "CREATE TABLE IF NOT EXISTS JobCardPending (Id INTEGER PRIMARY KEY AUTOINCREMENT, JobCardId TEXT, " +
                    "FarmBlockNurseryType TEXT, FarmBlockNurseryId TEXT, UniqueId TEXT, ZoneId TEXT, " +
                    "PlantationUniqueId TEXT, VisitDate TEXT, CreateBy TEXT, JobCardDetailId TEXT, " +
                    "FarmActivityId TEXT, FarmSubActivityId TEXT, ActivityValue TEXT, ActivityType " +
                    "TEXT, PlannedValue TEXT, ConfirmValue TEXT, Remarks TEXT,IsSync TEXT, CreateDate TEXT)",
            VisitReport_CREATE =
                    "CREATE TABLE IF NOT EXISTS VisitReport(Id INTEGER PRIMARY KEY AUTOINCREMENT, VisitUniqueId TEXT, FarmerUniqueId TEXT, FarmBlockNurseryUniqueId TEXT, FarmBlockNurseryType TEXT, ZoneId TEXT, PlantationId TEXT, Days TEXT, PlantHeight TEXT, PlantStatusId TEXT, Latitude TEXT, Longitude TEXT, Accuracy TEXT, CreateBy TEXT, CreateDate TEXT, IsSync TEXT, IsTemp TEXT)",
            VisitReportDetail_CREATE =
                    "CREATE TABLE IF NOT EXISTS VisitReportDetail(Id INTEGER PRIMARY KEY AUTOINCREMENT, VisitUniqueId TEXT, UniqueId TEXT, DefectId TEXT, Remarks TEXT, IsSync TEXT, IsTemp TEXT)",
            VisitReportPhoto_CREATE =
                    "CREATE TABLE IF NOT EXISTS VisitReportPhoto(Id INTEGER PRIMARY KEY AUTOINCREMENT, VisitUniqueId TEXT, UniqueId TEXT, DefectId TEXT, FileName TEXT, FilePath TEXT, IsSync TEXT, IsTemp TEXT)",
            Recommendation_CREATE =
                    "CREATE TABLE IF NOT EXISTS Recommendation(Id INTEGER PRIMARY KEY AUTOINCREMENT, UniqueId TEXT, FarmerUniqueId TEXT, FarmBlockNurseryUniqueId TEXT, FarmBlockNurseryType TEXT, ZoneId TEXT, PlantationId TEXT, Latitude TEXT, Longitude TEXT, Accuracy TEXT, CreateBy TEXT, CreateDate TEXT, IsSyncData TEXT, IsTemp TEXT)",
            RecommendationDetail_CREATE =
                    "CREATE TABLE IF NOT EXISTS RecommendationDetail(Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "RecommendationUniqueId TEXT, UniqueId TEXT, FarmActivityId TEXT, FarmSubActivityId TEXT, UomId TEXT, " +
                            "WeekNo TEXT, Remarks TEXT, ActivityValue TEXT, FileName TEXT, IsSyncData TEXT, IsSyncFile TEXT, IsTemp TEXT, JCWeek TEXT)",
            PlantStatus_CREATE =
                    "CREATE TABLE IF NOT EXISTS PlantStatus(Id TEXT, Title TEXT, TitleHindi TEXT);",
            Defect_CREATE =
                    "CREATE TABLE IF NOT EXISTS Defect(Id TEXT, Title TEXT, TitleHindi TEXT);",
            PlantationWeek_CREATE =
                    "CREATE TABLE IF NOT EXISTS PlantationWeek(PlantationUniqueId TEXT, WeekNo TEXT, FromDate TEXT, ToDate TEXT);",
            BookingAddressTemp_CREATE = "CREATE TABLE IF NOT EXISTS BookingAddressTemp(UniqueId TEXT, Street1 TEXT, Street2 TEXT, " +
                    "StateId TEXT, DistrictId TEXT, BlockId TEXT, PanchayatId TEXT, VillageId TEXT, CityId TEXT, PinCodeId TEXT, AddressType TEXT)",
            PaymentMode_CREATE =
                    "CREATE TABLE IF NOT EXISTS PaymentMode(Id TEXT, Title TEXT, TitleHindi TEXT);",
            PolyBagRate_CREATE =
                    "CREATE TABLE IF NOT EXISTS PolyBagRate(DistrictId TEXT, NurseryId TEXT, RateDate TEXT, Rate TEXT);",
            Booking_CREATE =
                    "CREATE TABLE IF NOT EXISTS Booking(Id INTEGER PRIMARY KEY AUTOINCREMENT, BookingUniqueId TEXT, " +
                            "BookingFor TEXT, BookingForId TEXT, FarmBlockId TEXT, Street1 TEXT, Street2 TEXT, StateId TEXT, DistrictId TEXT, " +
                            "BlockId TEXT, PanchayatId TEXT, VillageId TEXT, CityId TEXT, PinCodeId TEXT, AddressType TEXT, BookingDate TEXT, " +
                            "DeliveryDate TEXT, Quantity TEXT, Rate TEXT, TotalAmount TEXT, PaymentMode TEXT, PaymentAmount TEXT, Remarks TEXT, " +
                            "FileName TEXT, FilePath TEXT, BalanceQuantity TEXT, ShortCloseQuantity TEXT, ShortCloseReasonId TEXT, ShortCloseBy TEXT, " +
                            "ShortCloseDate TEXT, CreateBy TEXT, CreateDate TEXT, Latitude TEXT, " +
                            "Longitude TEXT, Accuracy TEXT, IsSync TEXT);",
            ShortCloseReason_CREATE =
                    "CREATE TABLE IF NOT EXISTS ShortCloseReason(Id TEXT, Title TEXT, TitleLocal TEXT);",
            PendingDispatchForDelivery_CREATE =
                    "CREATE TABLE IF NOT EXISTS PendingDispatchForDelivery(Id TEXT, Code TEXT, " +
                            "DispatchForId TEXT, DispatchForName TEXT, DispatchForMobile TEXT, " +
                            "VehicleNo TEXT, DriverName TEXT, DriverMobileNo TEXT, ShortCloseReasonId TEXT);",
            PendingDispatchDetailsForDelivery_CREATE = "CREATE TABLE IF NOT EXISTS " +
                    "PendingDispatchDetailsForDelivery (DispatchId TEXT, BookingId TEXT, Rate TEXT, " +
                    "PolybagTypeId TEXT, PolybagTitle TEXT, Quantity INTEGER)",
            DeliveryDetailsForDispatch_CREATE = "CREATE TABLE IF NOT EXISTS DeliveryDetailsForDispatch (DispatchId TEXT, BookingId TEXT, " +
                    "DispatchItemId TEXT, Quantity INTEGER,UniqueId TEXT)",
            BalanceDetailsForFarmerNursery_CREATE = "CREATE TABLE IF NOT EXISTS BalanceDetailsForFarmerNursery (FarmerNursery TEXT, " +
                    "FarmerNurseryId TEXT, BalanceAmount TEXT)",
            PaymentAgainstDispatchDelivery_CREATE = "CREATE TABLE IF NOT EXISTS PaymentAgainstDispatchDelivery (DispatchId, TEXT, BookingId TEXT, " +
                    "TotalAmount TEXT, TotalBalance TEXT, PaymentMode TEXT, PaymentAmount TEXT, PaymentRemarks TEXT)";

    static String prevfarmCroppingUniqueId = "";
    /*Context of the application using the database.*/
    private final Context context;
    /*Variable to hold the database instance*/
    public SQLiteDatabase db;
    String userlang;
    ContentValues newValues = null;
    HashMap<String, String> map = null;
    HashMap<String, String> mapcharacteristic = null;
    HashMap<String, String> mapissue = null;
    private String result = null;
    private String selectQuery = null;
    private UserSessionManager session;
    private Cursor cursor, cursorLandCharacteristic, cursorLandIssue;
    private ArrayList<HashMap<String, String>> wordList = null;

    /*Database open/upgrade helper*/
    private DatabaseHelper dbHelper;

    /*Class Constructor*/
    public DatabaseAdapter(Context _context) {
        context = _context;
        dbHelper = new DatabaseHelper(context, DATABASE_NAME, null,
                DATABASE_VERSION);
        session = new UserSessionManager(_context);

        userlang = session.getDefaultLang();
    }

    /*Open database in writable mode*/
    public DatabaseAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    /*Open database in readable mode*/
    public DatabaseAdapter openR() throws SQLException {
        db = dbHelper.getReadableDatabase();
        return this;
    }

    /*Close Database*/
    public void close() {
        db.close();
    }

    /*Check if the database is open*/
    public boolean isOpen() {
        return db.isOpen();
    }

    /*Get SQLLite Instance of database*/
    // TO DO: Remove the method if not used anywhere in the database
    /*public SQLiteDatabase getDatabaseInstance() {
        return db;
    }*/


    //<editor-fold desc="Method to get current date time">
    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }
    //</editor-fold>

    //<editor-fold desc="Get the version">
    public String getVersion() {
        return "VLPS17112017";
    }
    //</editor-fold>


    //<editor-fold desc="Method to Insert Version in the database">
    public String insertVersion(String message, String activityName, String methodName) {
        result = "fail";
        newValues = new ContentValues();

        String PhoneModel = android.os.Build.MODEL;
        String AndroidVersion = android.os.Build.VERSION.RELEASE;
        String deviceMan = android.os.Build.MANUFACTURER;

        newValues.put("Message", message);
        newValues.put("ActivityName", activityName);
        newValues.put("CalledMethod", methodName);
        newValues.put("CreatedOn", getDateTime());
        newValues.put("PhoneInfo", deviceMan + " " + PhoneModel + " "
                + AndroidVersion);
        db = dbHelper.getWritableDatabase();
        db.insert("Exceptions", null, newValues);
        result = "success";
        // cursor.close();
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert exception details in Exception Table">
    public String insertExceptions(String message, String activityName, String methodName) {
        result = "fail";
        newValues = new ContentValues();

        String PhoneModel = android.os.Build.MODEL;
        String AndroidVersion = android.os.Build.VERSION.RELEASE;
        String deviceMan = android.os.Build.MANUFACTURER;

        newValues.put("Message", message);
        newValues.put("ActivityName", activityName);
        newValues.put("CalledMethod", methodName);
        newValues.put("CreatedOn", getDateTime());
        newValues.put("PhoneInfo", deviceMan + " " + PhoneModel + " "
                + AndroidVersion);
        db = dbHelper.getWritableDatabase();
        db.insert("Exceptions", null, newValues);
        result = "success";

        // cursor.close();
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert farmer details in Temp farmer table">
    public String insertFarmerTemp(String farmerUniqueId, String educationLevelId, String farmerTypeId, String salutation, String firstName, String middleName, String lastName,
                                   String fatherSalutation, String fatherFirstName, String fatherMiddleName, String fatherLastName, String emailId, String mobile, String mobile1, String mobile2,
                                   String birthDate, String gender, String bankAccountNo, String ifscCCode, String totalAcreage, String fSSAINumber, String registrationDate, String expiryDate,
                                   String addressType, String street1, String street2, String stateId, String districtId, String blockId, String panchayatId, String villageId, String cityId, String pinCodeId,
                                   String createBy, String language, String soilTypeId, String irrigationSystemId, String riverId, String damId, String waterSourceId, String electricitySourceId,
                                   String longitude, String latitude, String accuracy) {
        result = "fail";
        newValues = new ContentValues();
        newValues.put("FarmerUniqueId", farmerUniqueId);
        newValues.put("EducationLevelId", educationLevelId);
        newValues.put("FarmerTypeId", farmerTypeId);
        newValues.put("Salutation", salutation);
        newValues.put("FirstName", firstName);
        newValues.put("MiddleName", middleName);
        newValues.put("LastName", lastName);
        newValues.put("FatherSalutation", fatherSalutation);
        newValues.put("FatherFirstName", fatherFirstName);
        newValues.put("FatherMiddleName", fatherMiddleName);
        newValues.put("FatherLastName", fatherLastName);
        newValues.put("EmailId", emailId);
        newValues.put("Mobile", mobile);
        newValues.put("Mobile1", mobile1);
        newValues.put("Mobile2", mobile2);
        newValues.put("BirthDate", birthDate);
        newValues.put("Gender", gender);
        newValues.put("BankAccountNo", bankAccountNo);
        newValues.put("IFSCCode", ifscCCode);
        newValues.put("TotalAcreage", totalAcreage);
        newValues.put("FSSAINumber", fSSAINumber);
        newValues.put("RegistrationDate", registrationDate);
        newValues.put("ExpiryDate", expiryDate);
        newValues.put("CreateBy", createBy);
        newValues.put("CreateDate", getDateTime());
        newValues.put("LanguageId", language);
        newValues.put("Longitude", longitude);
        newValues.put("Latitude", latitude);
        newValues.put("Accuracy", accuracy);
        db = dbHelper.getWritableDatabase();
        db.insert("FarmerTemp", null, newValues);

        newValues = new ContentValues();

        newValues.put("FarmerUniqueId", farmerUniqueId);
        newValues.put("AddressType", addressType);
        newValues.put("Street1", street1);
        newValues.put("Street2", street2);
        newValues.put("StateId", stateId);
        newValues.put("DistrictId", districtId);
        newValues.put("BlockId", blockId);
        newValues.put("PanchayatId", panchayatId);
        newValues.put("VillageId", villageId);
        newValues.put("CityId", cityId);
        newValues.put("PinCodeId", pinCodeId);
        db = dbHelper.getWritableDatabase();
        db.insert("AddressTemp", null, newValues);

        if (soilTypeId != "0" || irrigationSystemId != "0" || riverId != "0" || damId != "0" || waterSourceId != "0" || electricitySourceId != "0") {
            newValues = new ContentValues();
            newValues.put("FarmerUniqueId", farmerUniqueId);
            newValues.put("SoilTypeId", soilTypeId);
            newValues.put("IrrigationSystemId", irrigationSystemId);
            newValues.put("RiverId", riverId);
            newValues.put("DamId", damId);
            newValues.put("WaterSourceId", waterSourceId);
            newValues.put("ElectricitySourceId", electricitySourceId);
            db = dbHelper.getWritableDatabase();
            db.insert("FarmerOtherDetailsTemp", null, newValues);
        }

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to Update farmer details in farmer table">
    public String updateTempFarmer(String farmerUniqueId, String educationLevelId, String farmerTypeId, String salutation, String firstName, String middleName, String lastName,
                                   String fatherSalutation, String fatherFirstName, String fatherMiddleName, String fatherLastName, String emailId, String mobile, String mobile1, String mobile2,
                                   String birthDate, String gender, String bankAccountNo, String ifscCCode, String totalAcreage, String fSSAINumber, String registrationDate, String expiryDate,
                                   String addressType, String street1, String street2, String stateId, String districtId, String blockId, String panchayatId, String villageId, String cityId, String pinCodeId,
                                   String createBy, String language, String soilTypeId, String irrigationSystemId, String riverId, String damId, String waterSourceId, String electricitySourceId,
                                   String longitude, String latitude, String accuracy) {
        result = "fail";
        selectQuery = "UPDATE FarmerTemp SET EducationLevelId= '" + educationLevelId + "',FarmerTypeId= '" + farmerTypeId + "',Salutation= '" + salutation + "',FirstName= '" + firstName + "',MiddleName= '" + middleName + "',LastName= '" + lastName + "',FatherSalutation= '" + fatherSalutation + "',FatherFirstName= '" + fatherFirstName + "',FatherMiddleName= '" + fatherMiddleName + "',FatherLastName= '" + fatherLastName + "',EmailId= '" + emailId + "',Mobile= '" + mobile + "',Mobile1= '" + mobile1 + "',Mobile2= '" + mobile2 + "',BirthDate= '" + birthDate + "',Gender= '" + gender + "',BankAccountNo= '" + bankAccountNo + "',IFSCCode= '" + ifscCCode + "',TotalAcreage= '" + totalAcreage + "',FSSAINumber= '" + fSSAINumber + "',RegistrationDate= '" + registrationDate + "',ExpiryDate= '" + expiryDate + "',LanguageId= '" + language + "',Longitude= '" + longitude + "', Latitude= '" + latitude + "',Accuracy= '" + accuracy + "' WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
        db.execSQL(selectQuery);
        db.execSQL("UPDATE AddressTemp SET AddressType ='" + addressType + "',Street1='" + street1 + "',Street2='" + street2 + "',StateId='" + stateId + "',DistrictId='" + districtId + "',BlockId='" + blockId + "', PanchayatId='" + panchayatId + "',VillageId='" + villageId + "', PinCodeId='" + pinCodeId + "' WHERE FarmerUniqueId ='" + farmerUniqueId + "'; ");
        db.execSQL("DELETE FROM FarmerOtherDetailsTemp  WHERE FarmerUniqueId ='" + farmerUniqueId + "'; ");

        if (soilTypeId != "0" || irrigationSystemId != "0" || riverId != "0" || damId != "0" || waterSourceId != "0" || electricitySourceId != "0") {

            newValues = new ContentValues();
            newValues.put("FarmerUniqueId", farmerUniqueId);
            newValues.put("SoilTypeId", soilTypeId);
            newValues.put("IrrigationSystemId", irrigationSystemId);
            newValues.put("RiverId", riverId);
            newValues.put("DamId", damId);
            newValues.put("WaterSourceId", waterSourceId);
            newValues.put("ElectricitySourceId", electricitySourceId);
            db = dbHelper.getWritableDatabase();
            db.insert("FarmerOtherDetailsTemp", null, newValues);
        }


        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to Update farmer details in farmer table">
    public String updateFarmerTemp(String farmerUniqueId, String educationLevelId, String farmerTypeId, String salutation, String firstName, String middleName, String lastName,
                                   String fatherSalutation, String fatherFirstName, String fatherMiddleName, String fatherLastName, String emailId, String mobile, String mobile1, String mobile2,
                                   String birthDate, String gender, String bankAccountNo, String ifscCCode, String totalAcreage, String fSSAINumber, String registrationDate, String expiryDate,
                                   String addressType, String street1, String street2, String stateId, String districtId, String blockId, String panchayatId, String villageId, String cityId, String pinCodeId,
                                   String createBy, String language, String soilTypeId, String irrigationSystemId, String riverId, String damId, String waterSourceId, String electricitySourceId,
                                   String longitude, String latitude, String accuracy) {
        result = "fail";
        selectQuery = "UPDATE Farmer SET EducationLevelId= '" + educationLevelId + "',FarmerTypeId= '" + farmerTypeId + "',Salutation= '" + salutation + "',FirstName= '" + firstName + "',MiddleName= '" + middleName + "',LastName= '" + lastName + "',FatherSalutation= '" + fatherSalutation + "',FatherFirstName= '" + fatherFirstName + "',FatherMiddleName= '" + fatherMiddleName + "',FatherLastName= '" + fatherLastName + "',EmailId= '" + emailId + "',Mobile= '" + mobile + "',Mobile1= '" + mobile1 + "',Mobile2= '" + mobile2 + "',BirthDate= '" + birthDate + "',Gender= '" + gender + "',BankAccountNo= '" + bankAccountNo + "',IFSCCode= '" + ifscCCode + "',TotalAcreage= '" + totalAcreage + "',FSSAINumber= '" + fSSAINumber + "',RegistrationDate= '" + registrationDate + "',ExpiryDate= '" + expiryDate + "',LanguageId= '" + language + "',Longitude= '" + longitude + "', Latitude= '" + latitude + "',Accuracy= '" + accuracy + "',IsSync='' WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
        db.execSQL(selectQuery);
        db.execSQL("UPDATE Address SET AddressType ='" + addressType + "',Street1='" + street1 + "',Street2='" + street2 + "',StateId='" + stateId + "',DistrictId='" + districtId + "',BlockId='" + blockId + "', PanchayatId='" + panchayatId + "',VillageId='" + villageId + "', PinCodeId='" + pinCodeId + "' WHERE FarmerUniqueId ='" + farmerUniqueId + "'; ");
        db.execSQL("DELETE FROM FarmerOtherDetails  WHERE FarmerUniqueId ='" + farmerUniqueId + "'; ");

        if (soilTypeId != "0" || irrigationSystemId != "0" || riverId != "0" || damId != "0" || waterSourceId != "0" || electricitySourceId != "0") {

            newValues = new ContentValues();
            newValues.put("FarmerUniqueId", farmerUniqueId);
            newValues.put("SoilTypeId", soilTypeId);
            newValues.put("IrrigationSystemId", irrigationSystemId);
            newValues.put("RiverId", riverId);
            newValues.put("DamId", damId);
            newValues.put("WaterSourceId", waterSourceId);
            newValues.put("ElectricitySourceId", electricitySourceId);
            db = dbHelper.getWritableDatabase();
            db.insert("FarmerOtherDetails", null, newValues);
        }
        //Code added for Issue In Image Synchronizing
        db.execSQL("DELETE FROM FarmerDocuments WHERE FarmerUniqueId = '" + farmerUniqueId + "';");
        db.execSQL("INSERT INTO FarmerDocuments(FarmerUniqueId,Type, FileName) SELECT '" + farmerUniqueId + "', Type,FileName FROM TempFile ");

        db.execSQL("DELETE FROM FarmerTemp;");
        db.execSQL("DELETE FROM AddressTemp;");
        db.execSQL("DELETE FROM FarmerOperatingBlocksTemp;");
        db.execSQL("DELETE FROM FarmerOtherDetailsTemp;");
        db.execSQL("DELETE FROM TempFile;");

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to Update farmer details in farmer table">
    public String updateFarmerDetails(String farmerUniqueId, String educationLevelId, String emailId, String mobile1, String mobile2, String bankAccountNo,
                                      String ifscCCode, String totalAcreage, String fSSAINumber, String registrationDate, String expiryDate, String longitude, String latitude, String accuracy) {
        result = "fail";
        selectQuery = "UPDATE Farmer SET EducationLevelId= '" + educationLevelId + "',EmailId= '" + emailId + "',Mobile1= '" + mobile1 + "',Mobile2= '" + mobile2 + "',BankAccountNo= '" + bankAccountNo + "',IFSCCode= '" + ifscCCode + "',TotalAcreage= '" + totalAcreage + "',FSSAINumber= '" + fSSAINumber + "',RegistrationDate= '" + registrationDate + "',ExpiryDate= '" + expiryDate + "',Longitude='" + longitude + "', Latitude='" + latitude + "',Accuracy='" + accuracy + "',IsSync= '',CreateDate='" + getDateTime() + "' WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
        db.execSQL(selectQuery);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to Update farmer loan status details in farmer table">
    public String updateLoanStatus(String farmerUniqueId) {
        result = "fail";
        selectQuery = "UPDATE Farmer SET IsLoanNotApplicable = 1 WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
        db.execSQL(selectQuery);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="To Update New Farmer IsSync flag">
    public String Update_NewFarmerIsSync() {
        try {
            String query = "UPDATE Farmer SET IsSync = '1' WHERE FarmerCode IS NULL AND IsSync IS NULL ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="To Update Old Farmer IsSync flag">
    public String Update_FarmerIsSync() {
        try {
            String query = "UPDATE Farmer SET IsSync = '1' WHERE FarmerCode IS NOT NULL AND (IsSync IS NULL OR IsSync ='') ";
            db.execSQL(query);
            db.execSQL("UPDATE FarmerDocuments SET IsSync = '1' WHERE FarmerCode IS NOT NULL AND (IsSync IS NULL OR IsSync ='') ");
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="To Update New FarmBlock IsSync flag">
    public String Update_NewFarBlockIsSync() {
        try {
            String query = "UPDATE FarmBlock SET IsSync = '1' WHERE FarmBlockCode IS NULL AND IsSync IS NULL ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="To Update Existing FarmBlock IsSync flag">
    public String Update_ExistingFarBlockIsSync() {
        try {
            String query = "UPDATE FarmBlock SET IsSync = '1' WHERE FarmBlockCode IS NOT NULL AND IsSync='' ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="To Update Existing Plantation IsSync flag">
    public String Update_ExistingPlantationIsSync() {
        try {
            String query = "UPDATE FarmerPlantation SET IsSync = '1' WHERE IsSync IS NULL OR IsSync='' ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="To Update Existing InterCropping IsSync flag">
    public String Update_ExistingIntercroppingIsSync() {
        try {
            String query = "UPDATE InterCropping SET IsSync = '1' WHERE IsSync IS NULL OR IsSync='' ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="To Update Existing Nursery Plantation IsSync flag">
    public String Update_ExistingNurseryPlantationIsSync() {
        try {
            String query = "UPDATE NurseryPlantation SET IsSync = '1' WHERE IsSync IS NULL OR IsSync='' ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="To Update Existing NurseryInterCropping IsSync flag">
    public String Update_ExistingNurseryInterCroppingIsSync() {
        try {
            String query = "UPDATE NurseryInterCropping SET IsSync = '1' WHERE IsSync IS NULL OR IsSync='' ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in UserRoles Table">
    public String insertUserRoles(String userId, String userName, String roleName, String stateId, String districtId, String blockId, String nurseryId) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("UserId", userId);
        newValues.put("UserName", userName);
        newValues.put("RoleName", roleName);
        newValues.put("StateId", stateId);
        newValues.put("DistrictId", districtId);
        newValues.put("BlockId", blockId);
        newValues.put("NurseryId", nurseryId);

        db = dbHelper.getWritableDatabase();
        db.insert("UserRoles", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in State Table">
    public String insertState(String stateId, String stateName, String shortName, String stateCode, String stateNameLocal) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("StateId", stateId);
        newValues.put("StateName", stateName);
        newValues.put("ShortName", shortName);
        newValues.put("StateCode", stateCode);
        newValues.put("StateNameLocal", stateNameLocal);

        db = dbHelper.getWritableDatabase();
        db.insert("State", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in District Table">
    public String insertDistrict(String districtId, String stateId, String districtName, String districtNameLocal) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("DistrictId", districtId);
        newValues.put("StateId", stateId);
        newValues.put("DistrictName", districtName);
        newValues.put("DistrictNameLocal", districtNameLocal);

        db = dbHelper.getWritableDatabase();
        db.insert("District", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in City Table">
    public String insertCity(String cityId, String districtId, String cityName, String cityNameLocal) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("CityId", cityId);
        newValues.put("DistrictId", districtId);
        newValues.put("CityName", cityName);
        newValues.put("CityNameLocal", cityNameLocal);

        db = dbHelper.getWritableDatabase();
        db.insert("City", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Block Table">
    public String insertBlock(String blockId, String districtId, String blockName, String blockNameLocal) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("BlockId", blockId);
        newValues.put("DistrictId", districtId);
        newValues.put("BlockName", blockName);
        newValues.put("BlockNameLocal", blockNameLocal);

        db = dbHelper.getWritableDatabase();
        db.insert("Block", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Panchayat Table">
    public String insertPanchayat(String panchayatId, String blockId, String panchayatName, String panchayatNameLocal) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("PanchayatId", panchayatId);
        newValues.put("BlockId", blockId);
        newValues.put("PanchayatName", panchayatName);
        newValues.put("PanchayatNameLocal", panchayatNameLocal);

        db = dbHelper.getWritableDatabase();
        db.insert("Panchayat", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Village Table">
    public String insertVillage(String villageId, String panchayatId, String villageName, String villageNameLocal) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("VillageId", villageId);
        newValues.put("PanchayatId", panchayatId);
        newValues.put("VillageName", villageName);
        newValues.put("VillageNameLocal", villageNameLocal);

        db = dbHelper.getWritableDatabase();
        db.insert("Village", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in PinCode Table">
    public String insertPinCode(String pinCodeId, String stateId, String districtId, String cityId, String blockId, String panchayatId, String villageId, String pinCode) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("PinCodeId", pinCodeId);
        newValues.put("StateId", stateId);
        newValues.put("DistrictId", districtId);
        newValues.put("CityId", cityId);
        newValues.put("BlockId", blockId);
        newValues.put("PanchayatId", panchayatId);
        newValues.put("VillageId", villageId);
        newValues.put("PinCode", pinCode);

        db = dbHelper.getWritableDatabase();
        db.insert("PinCode", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    public String insertShortCloseReason(String id, String title, String titleLocal) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleLocal", titleLocal);

        db = dbHelper.getWritableDatabase();
        db.insert("ShortCloseReason", null, newValues);
        result = "success";
        return result;
    }

    //<editor-fold desc="Method to insert details in Farmer Type Table">
    public String insertFarmerType(String farmerTypeId, String farmerType, String farmerTypeLocal) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmerTypeId", farmerTypeId);
        newValues.put("FarmerType", farmerType);
        newValues.put("FarmerTypeLocal", farmerTypeLocal);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmerType", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Education Level Table">
    public String insertEducationLevel(String educationLevelId, String educationLevel, String educationLevelLocal) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("EducationLevelId", educationLevelId);
        newValues.put("EducationLevel", educationLevel);
        newValues.put("EducationLevelLocal", educationLevelLocal);

        db = dbHelper.getWritableDatabase();
        db.insert("EducationLevel", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Proof Type Table">
    public String insertProofType(String id, String name, String nameLocal) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Name", name);
        newValues.put("NameLocal", nameLocal);

        db = dbHelper.getWritableDatabase();
        db.insert("ProofType", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in POAPOI Table">
    public String insertPOAPOI(String id, String proofTypeId, String name, String nameLocal) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("ProofTypeId", proofTypeId);
        newValues.put("Name", name);
        newValues.put("NameLocal", nameLocal);

        db = dbHelper.getWritableDatabase();
        db.insert("POAPOI", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Organizer Table">
    public String insertOrganizer(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("Organizer", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Land Issue Table">
    public String insertLandIssue(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("LandIssue", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in PlantType Table">
    public String insertPlantType(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("PlantType", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Payment Mode Table">
    public String insertPaymentMode(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("PaymentMode", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in PolyBag Rate Table">
    public String insertPolyBagRate(String distId, String nsId, String rateDate, String rate) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("DistrictId", distId);
        newValues.put("NurseryId", nsId);
        newValues.put("RateDate", rateDate);
        newValues.put("Rate", rate);

        db = dbHelper.getWritableDatabase();
        db.insert("PolyBagRate", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in PlantingSystem Table">
    public String insertPlantingSystem(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("PlantingSystem", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Soil Type Table">
    public String insertSoilType(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("SoilType", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Land Type Table">
    public String insertLandType(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("LandType", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Existing Use Table">
    public String insertExistingUse(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("ExistingUse", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Community Use Table">
    public String insertCommunityUse(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("CommunityUse", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Existing Hazard Table">
    public String insertExistingHazard(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("ExistingHazard", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Nearest Dam Table">
    public String insertNearestDam(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("NearestDam", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Nearest River Table">
    public String insertNearestRiver(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("NearestRiver", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Electricity Source Table">
    public String insertElectricitySource(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("ElectricitySource", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Water Source Table">
    public String insertWaterSource(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("WaterSource", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Irrigation System Table">
    public String insertIrrigationSystem(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("IrrigationSystem", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Land Characteristic Table">
    public String insertLandCharacteristic(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("LandCharacteristic", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Season Table">
    public String insertSeason(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("Season", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Crop Table">
    public String insertCrop(String id, String title, String titleHindi, String isPlantation) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);
        newValues.put("IsPlantation", isPlantation);


        db = dbHelper.getWritableDatabase();
        db.insert("Crop", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in LoanSource Table">
    public String insertLoanSource(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("LoanSource", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in LoanType Table">
    public String insertLoanType(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("LoanType", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in FarmAsset Table">
    public String insertFarmAsset(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmAsset", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in RelationShip Table">
    public String insertRelationShip(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("RelationShip", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Variety Table">
    public String insertVariety(String id, String cropId, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("CropId", cropId);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("Variety", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Monthage Table">
    public String insertMonthage(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("Monthage", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert File Data in temporary Table">
    public String Insert_TempFile(String type, String fileName) {
        try {
            result = "fail";
            newValues = new ContentValues();
            newValues.put("Type", type);
            newValues.put("FileName", fileName);

            db.insert("TempFile", null, newValues);
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Legal Dispute Table">
    public String insertLegalDispute(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("LegalDispute", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Nursery  Table">
    public String insertNurseryZone(String id, String uniqueId, String nurseryId, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("UniqueId", uniqueId);
        newValues.put("NurseryId", nurseryId);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("NurseryZone", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in UOM Table">
    public String insertUOM(String uomId, String title, String shortName, String titleLocal, String shortNameLocal) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("UomId", uomId);
        newValues.put("Title", title);
        newValues.put("ShortName", shortName);
        newValues.put("TitleLocal", titleLocal);
        newValues.put("ShortNameLocal", shortNameLocal);

        db = dbHelper.getWritableDatabase();
        db.insert("UOM", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in FarmActivityType Table">
    public String insertFarmActivityType(String farmActivityTypeId, String title, String titleLocal) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmActivityTypeId", farmActivityTypeId);
        newValues.put("Title", title);
        newValues.put("TitleLocal", titleLocal);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmActivityType", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in FarmActivity Table">
    public String insertFarmActivity(String farmActivityId, String farmActivityTypeId, String title, String titleLocal, String isSubActivityAllowed, String uomId) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmActivityId", farmActivityId);
        newValues.put("FarmActivityTypeId", farmActivityTypeId);
        newValues.put("Title", title);
        newValues.put("TitleLocal", titleLocal);
        newValues.put("IsSubActivityAllowed", isSubActivityAllowed);
        newValues.put("UomId", uomId);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmActivity", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Farm Sub Activity Table">
    public String insertFarmSubActivity(String farmSubActivityId, String farmActivityId, String subActivityName, String subActivityNameLocal, String uomId) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmSubActivityId", farmSubActivityId);
        newValues.put("FarmActivityId", farmActivityId);
        newValues.put("SubActivityName", subActivityName);
        newValues.put("SubActivityNameLocal", subActivityNameLocal);
        newValues.put("UomId", uomId);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmSubActivity", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in PlantStatus Table">
    public String insertPlantStatus(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("PlantStatus", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Defect Table">
    public String insertDefect(String id, String title, String titleHindi) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);

        db = dbHelper.getWritableDatabase();
        db.insert("Defect", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Planned Activity Table">
    public String insertPlannedActivity(String farmBlockNurseryType, String farmBlockNurseryId, String plantationId, String plantationUniqueId, String plannerDetailId, String activityId, String subActivityId, String weekNo, String uomId, String quantity, String remarks, String parameterDetailId, String plantationDate, String fromDate, String toDate, String farmBlockNurseryUniqueId) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmBlockNurseryType", farmBlockNurseryType);
        newValues.put("FarmBlockNurseryId", farmBlockNurseryId);
        newValues.put("PlantationId", plantationId);
        newValues.put("PlantationUniqueId", plantationUniqueId);
        newValues.put("PlannerDetailId", plannerDetailId);
        newValues.put("ActivityId", activityId);
        newValues.put("SubActivityId", subActivityId);
        newValues.put("WeekNo", weekNo);
        newValues.put("UOMId", uomId);
        newValues.put("Quantity", quantity);
        newValues.put("Remarks", remarks);
        newValues.put("ParameterDetailId", parameterDetailId);
        newValues.put("PlantationDate", plantationDate);
        newValues.put("FromDate", fromDate);
        newValues.put("ToDate", toDate);
        newValues.put("FarmBlockNurseryUniqueId", farmBlockNurseryUniqueId);

        db = dbHelper.getWritableDatabase();
        db.insert("PlannedActivity", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Recommended Activity Table">
    public String insertRecommendedActivity(String farmBlockNurseryType, String farmBlockNurseryId, String plantationId, String plantationUniqueId, String activityId, String subActivityId, String weekNo, String uomId, String quantity, String remarks, String fromDate, String toDate, String farmBlockNurseryUniqueId) {
        result = "fail";
        db.execSQL("DELETE FROM PlannedActivity WHERE FarmBlockNurseryType='" + farmBlockNurseryType + "' AND FarmBlockNurseryId ='" + farmBlockNurseryId + "' AND PlantationId ='" + plantationId + "' AND PlantationUniqueId ='" + plantationUniqueId + "' AND ActivityId='" + activityId + "' AND SubActivityId='" + subActivityId + "' AND WeekNo='" + weekNo + "' AND FromDate='" + fromDate + "' AND ToDate='" + toDate + "' AND FarmBlockNurseryUniqueId='" + farmBlockNurseryUniqueId + "' ; ");


        newValues = new ContentValues();

        newValues.put("FarmBlockNurseryType", farmBlockNurseryType);
        newValues.put("FarmBlockNurseryId", farmBlockNurseryId);
        newValues.put("PlantationId", plantationId);
        newValues.put("PlantationUniqueId", plantationUniqueId);
        newValues.put("ActivityId", activityId);
        newValues.put("SubActivityId", subActivityId);
        newValues.put("WeekNo", weekNo);
        newValues.put("UOMId", uomId);
        newValues.put("Quantity", quantity);
        newValues.put("Remarks", remarks);
        newValues.put("FromDate", fromDate);
        newValues.put("ToDate", toDate);
        newValues.put("FarmBlockNurseryUniqueId", farmBlockNurseryUniqueId);


        db = dbHelper.getWritableDatabase();
        db.insert("RecommendedActivity", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in AllActivities Table">
    public String insertAllActivities(String farmBlockNurseryType, String farmBlockNurseryId, String plantationId, String plantationUniqueId, String activityId, String subActivityId, String uomId, String remarks, String farmBlockNurseryUniqueId, String weekNo, String quantity) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmBlockNurseryType", farmBlockNurseryType);
        newValues.put("FarmBlockNurseryId", farmBlockNurseryId);
        newValues.put("PlantationId", plantationId);
        newValues.put("PlantationUniqueId", plantationUniqueId);
        newValues.put("ActivityId", activityId);
        newValues.put("SubActivityId", subActivityId);
        newValues.put("UomId", uomId);
        newValues.put("Remarks", remarks);
        newValues.put("FarmBlockNurseryUniqueId", farmBlockNurseryUniqueId);
        newValues.put("WeekNo", weekNo);
        newValues.put("Quantity", quantity);

        db = dbHelper.getWritableDatabase();
        db.insert("AllActivity", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Plantation table">
    public String insertFarmerPlantation(String plantationUniqueId, String farmerUniqueId, String farmBlockUniqueId, String zoneId, String cropId, String cropVarietyId,
                                         String plantTypeId, String monthAgeId, String acreage, String plantationDate, String plantingSystemId,
                                         String plantRow, String plantColumn, String balance, String totalPlant,
                                         String createBy, String longitude, String latitude, String accuracy) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("PlantationUniqueId", plantationUniqueId);
        newValues.put("FarmerUniqueId", farmerUniqueId);
        newValues.put("FarmBlockUniqueId", farmBlockUniqueId);
        newValues.put("ZoneId", zoneId);
        newValues.put("CropId", cropId);
        newValues.put("CropVarietyId", cropVarietyId);
        newValues.put("PlantTypeId", plantTypeId);
        newValues.put("MonthAgeId", monthAgeId);
        newValues.put("Acreage", acreage);
        newValues.put("PlantationDate", plantationDate);
        newValues.put("PlantingSystemId", plantingSystemId);
        newValues.put("PlantRow", plantRow);
        newValues.put("plantColumn", plantColumn);
        newValues.put("Balance", balance);
        newValues.put("TotalPlant", totalPlant);
        newValues.put("CreateBy", createBy);
        newValues.put("CreateDate", getDateTime());
        newValues.put("Longitude", longitude);
        newValues.put("Latitude", latitude);
        newValues.put("Accuracy", accuracy);
        db = dbHelper.getWritableDatabase();
        db.insert("FarmerPlantation", null, newValues);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert detail in InterCropping table">
    public String insertInterCropping(String interCroppingUniqueId, String farmerUniqueId, String farmBlockUniqueId, String farmerPlantationUniqueId, String cropVarietyId, String seasonId, String acreage, String createBy, String longitude, String latitude, String accuracy) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("InterCroppingUniqueId", interCroppingUniqueId);
        newValues.put("FarmerUniqueId", farmerUniqueId);
        newValues.put("FarmBlockUniqueId", farmBlockUniqueId);
        newValues.put("FarmerPlantationUniqueId", farmerPlantationUniqueId);
        newValues.put("CropVarietyId", cropVarietyId);
        newValues.put("SeasonId", seasonId);
        newValues.put("Acreage", acreage);
        newValues.put("CreateBy", createBy);
        newValues.put("CreateDate", getDateTime());
        newValues.put("Longitude", longitude);
        newValues.put("Latitude", latitude);
        newValues.put("Accuracy", accuracy);

        db = dbHelper.getWritableDatabase();
        db.insert("InterCropping", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Nursery Plantation table">
    public String insertNurseryPlantation(String plantationUniqueId, String nurseryId, String zoneId, String cropId, String cropVarietyId,
                                          String plantTypeId, String monthAgeId, String acreage, String plantationDate, String plantingSystemId,
                                          String plantRow, String plantColumn, String balance, String totalPlant,
                                          String createBy, String longitude, String latitude, String accuracy) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("PlantationUniqueId", plantationUniqueId);
        newValues.put("NurseryId", nurseryId);
        newValues.put("ZoneId", zoneId);
        newValues.put("CropId", cropId);
        newValues.put("CropVarietyId", cropVarietyId);
        newValues.put("PlantTypeId", plantTypeId);
        newValues.put("MonthAgeId", monthAgeId);
        newValues.put("Acreage", acreage);
        newValues.put("PlantationDate", plantationDate);
        newValues.put("PlantingSystemId", plantingSystemId);
        newValues.put("PlantRow", plantRow);
        newValues.put("plantColumn", plantColumn);
        newValues.put("Balance", balance);
        newValues.put("TotalPlant", totalPlant);
        newValues.put("CreateBy", createBy);
        newValues.put("CreateDate", getDateTime());
        newValues.put("Longitude", longitude);
        newValues.put("Latitude", latitude);
        newValues.put("Accuracy", accuracy);
        db = dbHelper.getWritableDatabase();
        db.insert("NurseryPlantation", null, newValues);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert detail in NurseryInterCropping table">
    public String insertNurseryInterCropping(String interCroppingUniqueId, String nurseryId, String plantationUniqueId,
                                             String cropVarietyId, String seasonId, String acreage, String createBy,
                                             String longitude, String latitude, String accuracy) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("InterCroppingUniqueId", interCroppingUniqueId);
        newValues.put("NurseryId", nurseryId);
        newValues.put("PlantationUniqueId", plantationUniqueId);
        newValues.put("CropVarietyId", cropVarietyId);
        newValues.put("SeasonId", seasonId);
        newValues.put("Acreage", acreage);
        newValues.put("CreateBy", createBy);
        newValues.put("CreateDate", getDateTime());
        newValues.put("Longitude", longitude);
        newValues.put("Latitude", latitude);
        newValues.put("Accuracy", accuracy);

        db = dbHelper.getWritableDatabase();
        db.insert("NurseryInterCropping", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Methods insert details of Pending Dispatch for Delivery">
    public void clearPendingDispatchForDelivery() {
        db.execSQL("DELETE FROM PendingDispatchForDelivery");
    }


    public String insertPendingDispatchForDelivery(String id, String code, String dispatchForId,
                                                   String dispatchForName, String
                                                           dispatchForMobile, String
                                                           vehicleNo, String driverName,
                                                   String driverMobileNo) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("Code", code);
        newValues.put("DispatchForId", dispatchForId);
        newValues.put("DispatchForName", dispatchForName);
        newValues.put("DispatchForMobile", dispatchForMobile);
        newValues.put("VehicleNo", vehicleNo);
        newValues.put("DriverName", driverName);
        newValues.put("DriverMobileNo", driverMobileNo);

        db = dbHelper.getWritableDatabase();
        db.insert("PendingDispatchForDelivery", null, newValues);
        result = "success";
        return result;
    }

    public void clearPendingDispatchDetailForDelivery() {
        db.execSQL("DELETE FROM PendingDispatchDetailsForDelivery");
    }

    public String insertPendingDispatchDetailForDelivery(String DispatchId, String BookingId, String
            Rate, String PolybagTypeId, String PolybagTitle, Integer Quantity) {

        result = "fail";
        newValues = new ContentValues();

        newValues.put("DispatchId", DispatchId);
        newValues.put("BookingId", BookingId);
        newValues.put("Rate", Rate);
        newValues.put("PolybagTypeId", PolybagTypeId);
        newValues.put("PolybagTitle", PolybagTitle);
        newValues.put("Quantity", Quantity);

        db = dbHelper.getWritableDatabase();
        db.insert("PendingDispatchDetailsForDelivery", null, newValues);
        result = "success";
        return result;
    }

    public void clearDeliveryDetailsForDispatch(String dispatchId) {
        if (dispatchId.trim().isEmpty())
            db.execSQL("DELETE FROM DeliveryDetailsForDispatch");
        else
            db.execSQL("DELETE FROM DeliveryDetailsForDispatch WHERE DispatchId = '" + dispatchId + "'");
    }

    public void clearPaymentAgainstDispatchDelivery(String dispatchId) {
        if (dispatchId.trim().isEmpty())
            db.execSQL("DELETE FROM PaymentAgainstDeliveryDispatch");
        else
            db.execSQL("DELETE FROM PaymentAgainstDeliveryDispatch WHERE '" + dispatchId + "' ");
    }

    public void clearBalanceDetailsForFarmerNursery() {
        db.execSQL("DELETE FROM BalanceDetailsForFarmerNursery");
    }

    public String insertBalanceDetailsForFarmerNursery(String farmerNursery, String farmerNurseryId, String balanceAmount) {

        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmerNursery", farmerNursery);
        newValues.put("FarmerNurseryId", farmerNurseryId);
        newValues.put("BalanceAmount", balanceAmount);

        db = dbHelper.getWritableDatabase();
        db.insert("BalanceDetailsForFarmerNursery", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to move File Data from temporary Table to FarmerDocuments table">
    public String Insert_FarmerDocuments(String farmerUniqueId) {
        try {

            result = "fail";
            db.execSQL("DELETE FROM FarmerDocuments WHERE FarmerUniqueId = '" + farmerUniqueId + "'; ");
            db.execSQL("INSERT INTO FarmerDocuments(FarmerUniqueId,Type, FileName, IsSync) SELECT '" + farmerUniqueId + "', Type,FileName, IsSync FROM TempFile ");
            db.execSQL("DELETE FROM TempFile; ");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to move File Data from temporary Table to FarmerDocuments table">
    public String Update_FarmerDocuments(String farmerUniqueId) {
        try {

            result = "fail";
            db.execSQL("DELETE FROM FarmerDocuments WHERE FarmerUniqueId = '" + farmerUniqueId + "' AND Type IN ('FSSAI','PassBook') ");
            db.execSQL("INSERT INTO FarmerDocuments(FarmerUniqueId,Type, FileName) SELECT FarmerUniqueId, Type,FileName FROM TempFarmerDocument WHERE FarmerUniqueId = '" + farmerUniqueId + "'; ");
            db.execSQL("DELETE FROM TempFarmerDocument WHERE FarmerUniqueId = '" + farmerUniqueId + "'; ");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Farmer Proof data in temporary Table">
    public String Insert_FarmerProofTemp(String farmerUniqueId, String uniqueId, String poapoiId, String documentno, String fileName, String filePath) {
        try {
            result = "fail";
            newValues = new ContentValues();
            newValues.put("FarmerUniqueId", farmerUniqueId);
            newValues.put("UniqueId", uniqueId);
            newValues.put("POAPOIId", poapoiId);
            newValues.put("DocumentNo", documentno);
            newValues.put("FileName", fileName);
            newValues.put("FilePath", filePath);

            db.insert("FarmerProofTemp", null, newValues);
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Farmer Proof data in main Table">
    public String Insert_FarmerProof(String userId, String farmerUniqueId) {
        try {
            result = "fail";
            db.execSQL("Delete FROM FarmerProof WHERE FarmerUniqueId = '" + farmerUniqueId + "' ");
            db.execSQL("INSERT INTO FarmerProof(FarmerUniqueId, UniqueId, POAPOIId, DocumentNo, FileName, FilePath, CreateBy, CreateDate, IsSync) SELECT FarmerUniqueId, UniqueId, POAPOIId, DocumentNo, FileName, FilePath, '" + userId + "', '" + getDateTime() + "',IsSync FROM FarmerProofTemp WHERE FarmerUniqueId = '" + farmerUniqueId + "' ");
            db.execSQL("UPDATE Farmer SET IsSync ='' WHERE FarmerUniqueId = '" + farmerUniqueId + "' ");
            db.execSQL("Delete FROM FarmerProofTemp");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Farmer Proof data in main Table">
    public String Insert_FarmerMainToTemp(String farmerUniqueId) {
        try {
            result = "fail";

            db.execSQL("INSERT INTO FarmerProofTemp(FarmerUniqueId, UniqueId, POAPOIId, DocumentNo, FileName, FilePath, IsSync) SELECT FarmerUniqueId, UniqueId, POAPOIId, DocumentNo, FileName, FilePath, IsSync FROM FarmerProof WHERE FarmerUniqueId = '" + farmerUniqueId + "' ");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Languages data in main Table">
    public String insertLanguages(String id, String name, String nameLocal, String culture) {
        try {
            result = "fail";
            newValues = new ContentValues();
            newValues.put("Id", id);
            newValues.put("Name", name);
            newValues.put("NameLocal", nameLocal);
            newValues.put("Culture", culture);

            db.insert("Languages", null, newValues);
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Farmer Operating Bolcks in Temporary Table">
    public String insertFarmerOperatingBlocksTemp(String farmerUniqueId, String districtId, String blockId) {
        try {
            result = "fail";
            newValues = new ContentValues();
            newValues.put("FarmerUniqueId", farmerUniqueId);
            newValues.put("DistrictId", districtId);
            newValues.put("BlockId", blockId);

            db.insert("FarmerOperatingBlocksTemp", null, newValues);
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert FarmerDocument in TempFarmerDocument Temporary Table">
    public String insertTempFarmerDocument(String farmerUniqueId, String type, String FileName) {
        try {
            result = "fail";
            newValues = new ContentValues();
            newValues.put("FarmerUniqueId", farmerUniqueId);
            newValues.put("Type", type);
            newValues.put("FileName", FileName);

            db.insert("TempFarmerDocument", null, newValues);
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert farmer details in farmer table">
    public String insertFarmer(String farmerUniqueId) {
        result = "fail";
        db.execSQL("INSERT INTO Farmer(FarmerUniqueId,EducationLevelId,FarmerTypeId,Salutation,FirstName,MiddleName,LastName,FatherSalutation,FatherFirstName,FatherMiddleName,FatherLastName,EmailId,Mobile,Mobile1,Mobile2,BirthDate,Gender,BankAccountNo,IFSCCode,TotalAcreage,FSSAINumber,RegistrationDate,ExpiryDate,LanguageId,CreateBy,CreateDate,Longitude, Latitude,Accuracy) SELECT FarmerUniqueId,EducationLevelId,FarmerTypeId,Salutation,FirstName,MiddleName,LastName,FatherSalutation,FatherFirstName,FatherMiddleName,FatherLastName,EmailId,Mobile,Mobile1,Mobile2,BirthDate,Gender,BankAccountNo,IFSCCode,TotalAcreage,FSSAINumber,RegistrationDate,ExpiryDate,LanguageId,CreateBy,CreateDate,Longitude, Latitude,Accuracy FROM FarmerTemp WHERE FarmerUniqueId ='" + farmerUniqueId + "' ");
        db.execSQL("INSERT INTO  Address(FarmerUniqueId,Street1, Street2,StateId,DistrictId,BlockId,PanchayatId,VillageId,CityId,PinCodeId,AddressType) SELECT FarmerUniqueId,Street1, Street2,StateId,DistrictId,BlockId,PanchayatId,VillageId,CityId,PinCodeId,AddressType FROM AddressTemp WHERE FarmerUniqueId ='" + farmerUniqueId + "' ");
        db.execSQL("INSERT INTO FarmerOperatingBlocks(FarmerUniqueId, DistrictId, BlockId) SELECT FarmerUniqueId, DistrictId, BlockId FROM FarmerOperatingBlocksTemp WHERE FarmerUniqueId ='" + farmerUniqueId + "' ");
        db.execSQL("INSERT INTO FarmerOtherDetails(FarmerUniqueId,SoilTypeId,IrrigationSystemId,RiverId,DamId,WaterSourceId,ElectricitySourceId) SELECT FarmerUniqueId,SoilTypeId,IrrigationSystemId,RiverId,DamId,WaterSourceId,ElectricitySourceId FROM FarmerOtherDetailsTemp WHERE FarmerUniqueId ='" + farmerUniqueId + "' AND (SoilTypeId!='0' OR IrrigationSystemId!='0' OR RiverId!='0' OR DamId!='0' OR WaterSourceId!='0' OR ElectricitySourceId!='0') ");
        db.execSQL("INSERT INTO FarmerDocuments(FarmerUniqueId,Type, FileName, IsSync) SELECT '" + farmerUniqueId + "', Type,FileName, IsSync FROM TempFile ");

        db.execSQL("DELETE FROM FarmerTemp;");
        db.execSQL("DELETE FROM AddressTemp;");
        db.execSQL("DELETE FROM FarmerOperatingBlocksTemp;");
        db.execSQL("DELETE FROM FarmerOtherDetailsTemp;");
        db.execSQL("DELETE FROM TempFile;");

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert farmer Operating Blocks in FarmerOperatingBlocks Table">
    public String updateFarmerOperatingBlocks(String farmerUniqueId) {
        result = "fail";
        db.execSQL("DELETE FROM FarmerOperatingBlocks WHERE FarmerUniqueId ='" + farmerUniqueId + "';");
        db.execSQL("INSERT INTO FarmerOperatingBlocks(FarmerUniqueId, DistrictId, BlockId) SELECT FarmerUniqueId, DistrictId, BlockId FROM FarmerOperatingBlocksTemp WHERE FarmerUniqueId ='" + farmerUniqueId + "' ");
        db.execSQL("DELETE FROM FarmerOperatingBlocksTemp;");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert update Block Assignment Details">
    public String updateBlocksAssigned(String farmerUniqueId) {
        result = "fail";
        db.execSQL("DELETE FROM FarmerOperatingBlocks WHERE FarmerUniqueId ='" + farmerUniqueId + "';");
        db.execSQL("INSERT INTO FarmerOperatingBlocks(FarmerUniqueId, DistrictId, BlockId) SELECT FarmerUniqueId, DistrictId, BlockId FROM FarmerOperatingBlocksTemp WHERE FarmerUniqueId ='" + farmerUniqueId + "' ");
        db.execSQL("DELETE FROM FarmerOperatingBlocksTemp;");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Farmer Operation Blocks Detail data from main To Temp">
    public String Insert_MainOperationalBlockDetailToTemp(String farmerUniqueId) {
        try {
            result = "fail";

            db.execSQL("INSERT INTO FarmerOperatingBlocksTemp(FarmerUniqueId, DistrictId, BlockId) SELECT FarmerUniqueId, DistrictId, BlockId FROM FarmerOperatingBlocks WHERE FarmerUniqueId ='" + farmerUniqueId + "'  ");

            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Farmer Document data from main To Temp">
    public String Insert_MainDocTempDoc(String farmerUniqueId) {
        try {
            result = "fail";
            db.execSQL("DELETE FROM TempFile;");
            db.execSQL("INSERT INTO TempFile(Type, FileName, IsSync) SELECT Type,FileName, IsSync FROM FarmerDocuments WHERE FarmerUniqueId ='" + farmerUniqueId + "'  ");

            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Farmer Operating Blocks data from main To Temp">
    public String Insert_FarmerOperatingBlocks(String farmerUniqueId) {
        try {
            result = "fail";
            db.execSQL("INSERT INTO FarmerOperatingBlocksTemp(FarmerUniqueId, DistrictId, BlockId) SELECT FarmerUniqueId, DistrictId, BlockId FROM FarmerOperatingBlocks WHERE FarmerUniqueId ='" + farmerUniqueId + "' ");

            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to Update farmer code in farmer table">
    public String updateInterCropFinYear(String interCroppingUniqueId, String finYear) {
        result = "fail";

        selectQuery = "UPDATE InterCropping SET FinancialYear= '" + finYear + "' WHERE InterCroppingUniqueId ='" + interCroppingUniqueId + "' ";

        db.execSQL(selectQuery);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to Update FinancialYear in NurseryInterCropping table">
    public String updateNurseryInterCropFinYear(String interCroppingUniqueId, String finYear) {
        result = "fail";

        selectQuery = "UPDATE NurseryInterCropping " +
                "SET FinancialYear= '" + finYear + "' WHERE InterCroppingUniqueId ='" + interCroppingUniqueId + "' ";

        db.execSQL(selectQuery);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to Update farmer code in farmer table">
    public String updateFarmerCode(String farmerUniqueId, String farmerCode) {
        result = "fail";
        if (!farmerCode.equalsIgnoreCase("NA"))
            selectQuery = "UPDATE Farmer SET FarmerCode= '" + farmerCode + "',IsDuplicate= '' WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
        else
            selectQuery = "UPDATE Farmer SET IsDuplicate= '1',IsSync='' WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
        db.execSQL(selectQuery);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to Update Farm Block code in farmer table">
    public String updateFarmBlockCode(String farmBlockUniqueId, String farmBlockCode) {
        result = "fail";
        selectQuery = "UPDATE FarmBlock SET FarmBlockCode= '" + farmBlockCode + "' WHERE FarmBlockUniqueId ='" + farmBlockUniqueId + "' ";
        db.execSQL(selectQuery);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to Update PlantationCode code in farmer table">
    public String updatePlantationCode(String plantationUniqueId, String plantationCode) {
        result = "fail";
        selectQuery = "UPDATE FarmerPlantation SET PlantationCode= '" + plantationCode + "' WHERE PlantationUniqueId ='" + plantationUniqueId + "' ";
        db.execSQL(selectQuery);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert farm block details in farm block table">
    public String insertFarmBlock(String uniqueId, String farmerId, String landTypeId, String fPOId, String khataNo, String khasraNo, String contractDate, String acerage, String createBy, String street1, String street2, String stateId, String districtId, String blockId, String panchayatId, String villageId, String pinCodeId, String longitude, String latitude, String accuracy, String ownerName, String ownerMobile) {
        result = "fail";
        newValues = new ContentValues();
        newValues.put("FarmBlockUniqueId", uniqueId);
        newValues.put("FarmerId", farmerId);
        newValues.put("LandTypeId", landTypeId);
        newValues.put("FPOId", fPOId);
        newValues.put("AddressId", uniqueId);
        newValues.put("KhataNo", khataNo);
        newValues.put("KhasraNo", khasraNo);
        newValues.put("ContractDate", contractDate);
        newValues.put("Acerage", acerage);
        newValues.put("Longitude", longitude);
        newValues.put("Latitude", latitude);
        newValues.put("Accuracy", accuracy);
        newValues.put("CreateBy", createBy);
        newValues.put("OwnerName", ownerName);
        newValues.put("OwnerMobile", ownerMobile);
        newValues.put("CreateDate", getDateTime());
        db = dbHelper.getWritableDatabase();
        db.insert("FarmBlock", null, newValues);

        newValues = new ContentValues();
        newValues.put("FarmerUniqueId", uniqueId);
        newValues.put("AddressType", "District Based");
        newValues.put("Street1", street1);
        newValues.put("Street2", street2);
        newValues.put("StateId", stateId);
        newValues.put("DistrictId", districtId);
        newValues.put("BlockId", blockId);
        newValues.put("PanchayatId", panchayatId);
        newValues.put("VillageId", villageId);
        newValues.put("CityId", "0");
        newValues.put("PinCodeId", pinCodeId);
        db = dbHelper.getWritableDatabase();
        db.insert("Address", null, newValues);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to update farm block details in farm block table">
    public String updateFarmBlock(String uniqueId, String landTypeId, String fPOId, String khataNo, String khasraNo, String contractDate, String acerage, String street1, String street2, String stateId, String districtId, String blockId, String panchayatId, String villageId, String pinCodeId, String longitude, String latitude, String accuracy, String ownerName, String ownerMobile) {
        result = "fail";

        db.execSQL("UPDATE FarmBlock SET LandTypeId ='" + landTypeId + "',FPOId ='" + fPOId + "', KhataNo='" + khataNo + "', KhasraNo='" + khasraNo + "', ContractDate='" + contractDate + "', Acerage='" + acerage + "',Longitude='" + longitude + "',Latitude='" + latitude + "',Accuracy='" + accuracy + "',OwnerName='" + ownerName + "',OwnerMobile='" + ownerMobile + "',CreateDate='" + getDateTime() + "',IsSync='' WHERE FarmBlockUniqueId = '" + uniqueId + "' ");
        db.execSQL("UPDATE Address SET Street1 ='" + street1 + "', Street2='" + street2 + "', StateId='" + stateId + "', DistrictId='" + districtId + "', BlockId='" + blockId + "', PanchayatId='" + panchayatId + "', VillageId='" + villageId + "', PinCodeId='" + pinCodeId + "' WHERE FarmerUniqueId = '" + uniqueId + "' ");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to update farm block soil details in farm block table">
    public String updateFarmBlockSoil(String uniqueId, String soilTypeId, String elevationMSL, String pHChemical, String nitrogen, String potash, String phosphorus, String organicCarbonPerc, String magnesium, String calcium) {
        result = "fail";
        String query = "";
        query = "UPDATE FarmBlock SET SoilTypeId ='" + soilTypeId + "', ElevationMSL='" + elevationMSL + "', PHChemical='" + pHChemical + "', Nitrogen='" + nitrogen + "', Potash='" + potash + "', Phosphorus='" + phosphorus + "', OrganicCarbonPerc='" + organicCarbonPerc + "', Magnesium='" + magnesium + "', Calcium='" + calcium + "' WHERE FarmBlockUniqueId = '" + uniqueId + "' ";
        db.execSQL(query);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to update farm block other details in farm block table">
    public String updateFarmBlockOther(String uniqueId, String existingUseId, String communityUseId, String existingHazardId, String riverId, String damId, String irrigationId, String overheadTransmission, String legalDisputeId, String sourceWaterId, String electricitySourceId, String dripperSpacing, String dischargeRate) {
        result = "fail";
        String query = "";
        query = "UPDATE FarmBlock SET ExistingUseId ='" + existingUseId + "', CommunityUseId='" + communityUseId + "', ExistingHazardId='" + existingHazardId + "', RiverId='" + riverId + "', DamId='" + damId + "', IrrigationId='" + irrigationId + "', OverheadTransmission='" + overheadTransmission + "', LegalDisputeId='" + legalDisputeId + "', SourceWaterId='" + sourceWaterId + "', ElectricitySourceId='" + electricitySourceId + "', DripperSpacing='" + dripperSpacing + "', DischargeRate='" + dischargeRate + "' WHERE FarmBlockUniqueId = '" + uniqueId + "' ";
        db.execSQL(query);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Family Members Details in FarmerFamilyMemberTemp Table">
    public String insertFarmerFamilyMemberTemp(String farmerUniqueId, String memberName, String gender, String birthDate, String relationshipId, String isNominee, String nomineePercentage, String createBy) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmerUniqueId", farmerUniqueId);
        newValues.put("MemberName", memberName);
        newValues.put("Gender", gender);
        newValues.put("BirthDate", birthDate);
        newValues.put("RelationshipId", relationshipId);
        newValues.put("IsNominee", isNominee);
        newValues.put("NomineePercentage", nomineePercentage);
        newValues.put("CreateDate", getDateTime());
        newValues.put("CreateBy", createBy);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmerFamilyMemberTemp", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Farmer Loan Details in FarmerLoanDetailsTemp Table">
    public String insertFarmerLoanDetailsTemp(String farmerUniqueId, String loanSourceId, String loanTypeId, String roiPercentage, String loanAmount, String balanceAmount, String tenure, String createBy) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmerUniqueId", farmerUniqueId);
        newValues.put("LoanSourceId", loanSourceId);
        newValues.put("LoanTypeId", loanTypeId);
        newValues.put("ROIPercentage", roiPercentage);
        newValues.put("LoanAmount", loanAmount);
        newValues.put("BalanceAmount", balanceAmount);
        newValues.put("Tenure", tenure);
        newValues.put("CreateDate", getDateTime());
        newValues.put("CreateBy", createBy);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmerLoanDetailsTemp", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Farmer Family Details From Temp to Main Table">
    public String Insert_FarmerFamilyTempToMain(String farmerUniqueId) {
        try {
            result = "fail";
            db.execSQL("Delete FROM FarmerFamilyMember WHERE FarmerUniqueId = '" + farmerUniqueId + "' ");
            db.execSQL("INSERT INTO FarmerFamilyMember(FarmerUniqueId, MemberName, Gender, BirthDate, RelationshipId, IsNominee,NomineePercentage, CreateBy, CreateDate) SELECT FarmerUniqueId, MemberName, Gender, BirthDate, RelationshipId, IsNominee,NomineePercentage, CreateBy, CreateDate FROM FarmerFamilyMemberTemp WHERE FarmerUniqueId = '" + farmerUniqueId + "' ");
            db.execSQL("Delete FROM FarmerFamilyMemberTemp");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Farmer Loan Details From Temp to Main Table">
    public String Insert_FarmerLoanTempToMain(String farmerUniqueId) {
        try {
            result = "fail";
            db.execSQL("Delete FROM FarmerLoanDetails WHERE FarmerUniqueId = '" + farmerUniqueId + "' ");
            db.execSQL("INSERT INTO FarmerLoanDetails(FarmerUniqueId, LoanSourceId, LoanTypeId, ROIPercentage, LoanAmount, BalanceAmount, Tenure, CreateBy, CreateDate) SELECT FarmerUniqueId, LoanSourceId, LoanTypeId, ROIPercentage, LoanAmount, BalanceAmount, Tenure, CreateBy, CreateDate FROM FarmerLoanDetailsTemp WHERE FarmerUniqueId = '" + farmerUniqueId + "' ");
            db.execSQL("Delete FROM FarmerLoanDetailsTemp");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Farmer Family Details From Main to Temp Table">
    public String Insert_FarmerFamilyMainToTemp(String farmerUniqueId) {
        try {
            result = "fail";

            db.execSQL("INSERT INTO FarmerFamilyMemberTemp(FarmerUniqueId, MemberName, Gender, BirthDate, RelationshipId, IsNominee,NomineePercentage, CreateBy, CreateDate) SELECT FarmerUniqueId, MemberName, Gender, BirthDate, RelationshipId, IsNominee,NomineePercentage, CreateBy, CreateDate FROM FarmerFamilyMember WHERE FarmerUniqueId = '" + farmerUniqueId + "' ");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Farmer Loan Details From Main to Temp Table">
    public String Insert_FarmerLoanMainToTemp(String farmerUniqueId) {
        try {
            result = "fail";

            db.execSQL("INSERT INTO FarmerLoanDetailsTemp(FarmerUniqueId, LoanSourceId, LoanTypeId, ROIPercentage, LoanAmount, BalanceAmount, Tenure, CreateBy, CreateDate) SELECT FarmerUniqueId, LoanSourceId, LoanTypeId, ROIPercentage, LoanAmount, BalanceAmount, Tenure, CreateBy, CreateDate FROM FarmerLoanDetails WHERE FarmerUniqueId = '" + farmerUniqueId + "' ");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in FarmBlockLandCharacteristic Table">
    public String insertFarmBlockLandCharacteristic(String farmBlockUniqueId, String landCharacteristicId) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmBlockUniqueId", farmBlockUniqueId);
        newValues.put("LandCharacteristicId", landCharacteristicId);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmBlockLandCharacteristic", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in FarmBlockLandIssue Table">
    public String insertFarmBlockLandIssue(String farmBlockUniqueId, String landIssueId) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmBlockUniqueId", farmBlockUniqueId);
        newValues.put("LandIssueId", landIssueId);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmBlockLandIssue", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert coordinates in Temporary Table">
    public String Insert_TempGPS(String latitude, String longitude, String accuracy) {
        try {
            result = "fail";
            newValues = new ContentValues();
            newValues.put("Latitude", latitude);
            newValues.put("Longitude", longitude);
            newValues.put("Accuracy", accuracy);
            db.insert("TempGPS", null, newValues);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold

    //<editor-fold desc="Method to insert details in Farm Block Cropping Pattern Table">
    public String insertFarmBlockCroppingPattern(String farmBlockUniqueId, String cropId, String cropVarietyId, String seasonId, String acreage) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmBlockUniqueId", farmBlockUniqueId);
        newValues.put("CropId", cropId);
        newValues.put("CropVarietyId", cropVarietyId);
        newValues.put("SeasonId", seasonId);
        newValues.put("Acreage", acreage);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmBlockCroppingPattern", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in FarmBlockCoordinates Table">
    public String insertFarmBlockCoordinates(String farmBlockUniqueId) {
        result = "fail";
        db.execSQL("INSERT INTO FarmBlockCoordinates(FarmBlockUniqueId,Latitude,Longitude,Accuracy) SELECT '" + farmBlockUniqueId + "', Latitude,Longitude,Accuracy FROM TempGPS ORDER BY Id");
        db.execSQL("DELETE FROM TempGPS");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Farmer Asset Table">
    public String insertFarmerAssetsDetails(String farmerUniqueId, String farmAssetsId, String farmAssetsNo, String createBy) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmerUniqueId", farmerUniqueId);
        newValues.put("FarmAssetsId", farmAssetsId);
        newValues.put("FarmAssetsNo", farmAssetsNo);
        newValues.put("CreateDate", getDateTime());
        newValues.put("CreateBy", createBy);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmerAssetDetails", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farmer Operational Block Details from Server in Farmer Table">
    public String insertUpdateServerFarmerOperationalBlockDetails(String farmerUniqueId, String districtId, String blockId, String flag) {
        result = "fail";
        if (flag.equalsIgnoreCase("Yes")) {
            selectQuery = "DELETE FROM  FarmerOperatingBlocks WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
            db.execSQL(selectQuery);
        }
        newValues = new ContentValues();

        newValues.put("FarmerUniqueId", farmerUniqueId);
        newValues.put("DistrictId", districtId);
        newValues.put("BlockId", blockId);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmerOperatingBlocks", null, newValues);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farmer Address Details from Server in Address Table">
    public String insertUpdateServerFarmerAddress(String farmerUniqueId, String street1, String street2, String stateId, String districtId, String blockId, String panchayatId, String villageId, String cityId, String pinCodeId, String addressType) {
        result = "fail";

        Boolean dataExists = false;
        selectQuery = "SELECT FarmerUniqueId FROM Address WHERE FarmerUniqueId = '" + farmerUniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        if (dataExists.equals(false)) {
            newValues = new ContentValues();

            newValues.put("FarmerUniqueId", farmerUniqueId);
            newValues.put("AddressType", addressType);
            newValues.put("Street1", street1);
            newValues.put("Street2", street2);
            newValues.put("StateId", stateId);
            newValues.put("DistrictId", districtId);
            newValues.put("BlockId", blockId);
            newValues.put("PanchayatId", panchayatId);
            newValues.put("VillageId", villageId);
            newValues.put("CityId", cityId);
            newValues.put("PinCodeId", pinCodeId);
            db = dbHelper.getWritableDatabase();
            db.insert("Address", null, newValues);
        } else {
            selectQuery = "UPDATE Address SET Street2= '" + street2 + "',Street1= '" + street1 + "',StateId= '" + stateId + "',DistrictId= '" + districtId + "',BlockId= '" + blockId + "',PanchayatId= '" + panchayatId + "',VillageId= '" + villageId + "',CityId= '" + cityId + "',PinCodeId= '" + pinCodeId + "',AddressType= '" + addressType + "' WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
            db.execSQL(selectQuery);
        }
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farmer Details from Server in Farmer Table">
    public String insertUpdateServerFarmer(String farmerUniqueId, String farmerCode, String educationLevelId, String farmerTypeId, String salutation, String firstName, String middleName, String lastName, String fatherSalutation, String fatherFirstName, String fatherMiddleName, String fatherLastName, String emailId, String mobile, String mobile1, String mobile2, String birthDate, String gender, String bankAccountNo, String ifscCode, String totalAcreage, String fssaiNumber, String registrationDate, String expiryDate, String languageId, String salutationLocal, String firstNameLocal, String middleNameLocal, String lastNameLocal, String fatherSalutationLocal, String fatherFirstNameLocal, String fatherMiddleNameLocal, String fatherLastNameLocal) {
        result = "fail";

        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM Farmer WHERE FarmerUniqueId = '" + farmerUniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);
        newValues = new ContentValues();
        newValues.put("FarmerUniqueId", farmerUniqueId);
        db = dbHelper.getWritableDatabase();
        db.insert("FarmerSyncTable", null, newValues);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        if (dataExists.equals(false)) {
            newValues = new ContentValues();

            newValues.put("FarmerUniqueId", farmerUniqueId);
            newValues.put("FarmerCode", farmerCode);
            newValues.put("EducationLevelId", educationLevelId);
            newValues.put("FarmerTypeId", farmerTypeId);
            newValues.put("Salutation", salutation);
            newValues.put("FirstName", firstName);
            newValues.put("MiddleName", middleName);
            newValues.put("LastName", lastName);
            newValues.put("FatherSalutation", fatherSalutation);
            newValues.put("FatherFirstName", fatherFirstName);
            newValues.put("FatherMiddleName", fatherMiddleName);
            newValues.put("FatherLastName", fatherLastName);
            newValues.put("EmailId", emailId);
            newValues.put("Mobile", mobile);
            newValues.put("Mobile1", mobile1);
            newValues.put("Mobile2", mobile2);
            newValues.put("BirthDate", birthDate);
            newValues.put("Gender", gender);
            newValues.put("BankAccountNo", bankAccountNo);
            newValues.put("IFSCCode", ifscCode);
            newValues.put("TotalAcreage", totalAcreage);
            newValues.put("FSSAINumber", fssaiNumber);
            newValues.put("RegistrationDate", registrationDate);
            newValues.put("ExpiryDate", expiryDate);
            newValues.put("LanguageId", languageId);
            newValues.put("IsSync", "1");
            newValues.put("SalutationLocal", salutationLocal);
            newValues.put("FirstNameLocal", firstNameLocal);
            newValues.put("MiddleNameLocal", middleNameLocal);
            newValues.put("LastNameLocal", lastNameLocal);
            newValues.put("FatherSalutationLocal", fatherSalutationLocal);
            newValues.put("FatherFirstNameLocal", fatherFirstNameLocal);
            newValues.put("FatherMiddleNameLocal", fatherMiddleNameLocal);
            newValues.put("FatherLastNameLocal", fatherLastNameLocal);

            db = dbHelper.getWritableDatabase();
            db.insert("Farmer", null, newValues);


        } else {
            selectQuery = "UPDATE Farmer SET EducationLevelId= '" + educationLevelId + "',FarmerTypeId= '" + farmerTypeId + "',Salutation= '" + salutation + "',FirstName= '" + firstName + "',MiddleName= '" + middleName + "',LastName= '" + lastName + "',FatherSalutation= '" + fatherSalutation + "',FatherFirstName= '" + fatherFirstName + "',FatherMiddleName= '" + fatherMiddleName + "',FatherLastName= '" + fatherLastName + "',EmailId= '" + emailId + "',Mobile= '" + mobile + "',Mobile1= '" + mobile1 + "',Mobile2= '" + mobile2 + "',BirthDate= '" + birthDate + "',Gender= '" + gender + "',BankAccountNo= '" + bankAccountNo + "',IFSCCode= '" + ifscCode + "',TotalAcreage= '" + totalAcreage + "',FSSAINumber= '" + fssaiNumber + "',RegistrationDate= '" + registrationDate + "',ExpiryDate= '" + expiryDate + "',LanguageId= '" + languageId + "',IsSync='1',SalutationLocal ='" + salutationLocal + "',FirstNameLocal ='" + firstNameLocal + "',MiddleNameLocal ='" + middleNameLocal + "',LastNameLocal ='" + lastNameLocal + "',FatherSalutationLocal ='" + fatherSalutationLocal + "',FatherFirstNameLocal ='" + fatherFirstNameLocal + "',FatherMiddleNameLocal ='" + fatherMiddleNameLocal + "',FatherLastNameLocal ='" + fatherLastNameLocal + "' WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
            db.execSQL(selectQuery);
        }

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farmer Family from Server in FarmerFamilyMember Table">
    public String insertUpdateServerFarmerFamilyDetails(String farmerUniqueId, String memberName, String gender, String birthDate, String relationshipId, String isNominee, String nomineePercentage, String flag, String memberNameLocal) {
        result = "fail";
        if (flag.equalsIgnoreCase("Yes")) {
            selectQuery = "DELETE FROM  FarmerFamilyMember WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
            db.execSQL(selectQuery);
        }
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmerUniqueId", farmerUniqueId);
        newValues.put("MemberName", memberName);
        newValues.put("Gender", gender);
        newValues.put("BirthDate", birthDate);
        newValues.put("RelationshipId", relationshipId);
        newValues.put("IsNominee", isNominee);
        newValues.put("NomineePercentage", nomineePercentage);
        newValues.put("MemberNameLocal", memberNameLocal);


        db = dbHelper.getWritableDatabase();
        db.insert("FarmerFamilyMember", null, newValues);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farmer Loan from Server in FarmerLoanDetails Table">
    public String insertUpdateServerFarmerLoanDetails(String farmerUniqueId, String loanSourceId, String loanTypeId, String roiPercentage, String loanAmount, String balanceAmount, String tenure, String flag) {
        result = "fail";
        if (flag.equalsIgnoreCase("Yes")) {
            selectQuery = "DELETE FROM  FarmerLoanDetails WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
            db.execSQL(selectQuery);
        }
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmerUniqueId", farmerUniqueId);
        newValues.put("LoanSourceId", loanSourceId);
        newValues.put("LoanTypeId", loanTypeId);
        newValues.put("ROIPercentage", roiPercentage);
        newValues.put("LoanAmount", loanAmount);
        newValues.put("BalanceAmount", balanceAmount);
        newValues.put("Tenure", tenure);
        newValues.put("IsSync", "1");

        db = dbHelper.getWritableDatabase();
        db.insert("FarmerLoanDetails", null, newValues);

        result = "success";
        return result;
    }

    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farmer Proof from Server in FarmerProof Table">
    public String insertUpdateServerFarmerProofDetails(String farmerUniqueId, String uniqueId, String poapoiId, String documentno, String fileName, String flag) {
        result = "fail";
        if (flag.equalsIgnoreCase("Yes")) {
            selectQuery = "DELETE FROM  FarmerProof WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
            db.execSQL(selectQuery);
        }
        result = "fail";
        newValues = new ContentValues();
        newValues.put("FarmerUniqueId", farmerUniqueId);
        newValues.put("UniqueId", uniqueId);
        newValues.put("POAPOIId", poapoiId);
        newValues.put("DocumentNo", documentno);
        newValues.put("FileName", fileName);
        newValues.put("IsSync", "1");

        db.insert("FarmerProof", null, newValues);

        result = "success";
        return result;
    }

    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farmer Asset from Server in FarmerAssetDetails Table">
    public String insertUpdateServerFarmerAssetDetails(String farmerUniqueId, String farmAssetsId, String farmAssetsNo, String flag) {
        result = "fail";
        if (flag.equalsIgnoreCase("Yes")) {
            selectQuery = "DELETE FROM  FarmerAssetDetails WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
            db.execSQL(selectQuery);
        }
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmerUniqueId", farmerUniqueId);
        newValues.put("FarmAssetsId", farmAssetsId);
        newValues.put("FarmAssetsNo", farmAssetsNo);
        newValues.put("CreateDate", getDateTime());

        db = dbHelper.getWritableDatabase();
        db.insert("FarmerAssetDetails", null, newValues);

        result = "success";
        return result;
    }

    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farmer Other Details from Server in FarmerOtherDetails Table">
    public String insertUpdateServerFarmerOtherDetails(String farmerUniqueId, String soilTypeId, String irrigationSystemId, String riverId, String damId, String waterSourceId, String electricitySourceId) {
        result = "fail";

        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM FarmerOtherDetails WHERE FarmerUniqueId = '" + farmerUniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        if (dataExists.equals(false)) {
            newValues = new ContentValues();
            newValues.put("FarmerUniqueId", farmerUniqueId);
            newValues.put("SoilTypeId", soilTypeId);
            newValues.put("IrrigationSystemId", irrigationSystemId);
            newValues.put("RiverId", riverId);
            newValues.put("DamId", damId);
            newValues.put("WaterSourceId", waterSourceId);
            newValues.put("ElectricitySourceId", electricitySourceId);
            db = dbHelper.getWritableDatabase();
            db.insert("FarmerOtherDetails", null, newValues);


        } else {
            selectQuery = "UPDATE FarmerOtherDetails SET SoilTypeId= '" + soilTypeId + "',IrrigationSystemId= '" + irrigationSystemId + "',RiverId= '" + riverId + "',DamId= '" + damId + "',WaterSourceId= '" + waterSourceId + "',ElectricitySourceId= '" + electricitySourceId + "' WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
            db.execSQL(selectQuery);
        }
        return result;
    }

    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farmer Documents from Server in FarmerDocument Table">
    public String insertUpdateServerFarmerDocument(String farmerUniqueId, String type, String FileName, String flag) {
        result = "fail";
        if (flag.equalsIgnoreCase("Yes")) {
            selectQuery = "DELETE FROM  FarmerDocuments WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
            db.execSQL(selectQuery);
        }
        result = "fail";
        newValues = new ContentValues();
        newValues.put("FarmerUniqueId", farmerUniqueId);
        newValues.put("Type", type);
        newValues.put("FileName", FileName);
        newValues.put("IsSync", "1");

        db.insert("FarmerDocuments", null, newValues);

        result = "success";
        return result;
    }

    //</editor-fold>

    //<editor-fold desc="To Delete Data from Farmer Sync Table">
    public String DeleteFarmerSyncTable() {
        try {
            String query = "DELETE FROM FarmerSyncTable ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="To Delete Data from FarmBlock Sync Table">
    public String DeleteFarmBlockSyncTable() {
        try {
            String query = "DELETE FROM FarmBlockSyncTable ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="To Delete Data from Plantation Sync Table">
    public String DeletePlantationSyncTable() {
        try {
            String query = "DELETE FROM PlantationSyncTable ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to update farm block details in farm block table">
    public String updateFarmBlockById(String uniqueId, String khataNo, String khasraNo, String acerage, String longitude, String latitude, String accuracy) {
        result = "fail";

        db.execSQL("UPDATE FarmBlock SET KhataNo='" + khataNo + "', KhasraNo='" + khasraNo + "', Acerage='" + acerage + "',CreateDate='" + getDateTime() + "',Longitude= '" + longitude + "', Latitude= '" + latitude + "',Accuracy= '" + accuracy + "' ,IsSync='' WHERE FarmBlockUniqueId = '" + uniqueId + "' ");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert / Update farm block details in farm block table">
    public String insertUpdateServerFarmBlock(String uniqueId, String farmerId, String farmblockCode, String landTypeId, String fPOId, String khataNo, String khasraNo, String contractDate, String acerage, String soilTypeId, String elevationMSL, String pHChemical, String nitrogen, String potash, String phosphorus, String organicCarbonPerc, String magnesium, String calcium, String existingUseId, String communityUseId, String existingHazardId, String riverId, String damId, String irrigationId, String overheadTransmission, String legalDisputeId, String sourceWaterId, String electricitySourceId, String dripperSpacing, String dischargeRate, String ownerName, String ownerMobile, String jobCardAllowed) {
        result = "fail";
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM FarmBlock WHERE FarmBlockUniqueId = '" + uniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);
        newValues = new ContentValues();
        newValues.put("FarmBlockUniqueId", uniqueId);
        db = dbHelper.getWritableDatabase();
        db.insert("FarmBlockSyncTable", null, newValues);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        if (dataExists.equals(false)) {
            newValues = new ContentValues();
            newValues.put("FarmBlockUniqueId", uniqueId);
            newValues.put("FarmerId", farmerId);
            newValues.put("FarmBlockCode", farmblockCode);
            newValues.put("LandTypeId", landTypeId);
            newValues.put("FPOId", fPOId);
            newValues.put("AddressId", uniqueId);
            newValues.put("KhataNo", khataNo);
            newValues.put("KhasraNo", khasraNo);
            newValues.put("ContractDate", contractDate);
            newValues.put("Acerage", acerage);
            newValues.put("SoilTypeId", soilTypeId);
            newValues.put("ElevationMSL", elevationMSL);
            newValues.put("PHChemical", pHChemical);
            newValues.put("Nitrogen", nitrogen);
            newValues.put("Potash", potash);
            newValues.put("Phosphorus", phosphorus);
            newValues.put("OrganicCarbonPerc", organicCarbonPerc);
            newValues.put("Magnesium", magnesium);
            newValues.put("Calcium", calcium);
            newValues.put("ExistingUseId", existingUseId);
            newValues.put("CommunityUseId", communityUseId);
            newValues.put("ExistingHazardId", existingHazardId);
            newValues.put("RiverId", riverId);
            newValues.put("DamId", damId);
            newValues.put("IrrigationId", irrigationId);
            newValues.put("OverheadTransmission", overheadTransmission);
            newValues.put("LegalDisputeId", legalDisputeId);
            newValues.put("SourceWaterId", sourceWaterId);
            newValues.put("ElectricitySourceId", electricitySourceId);
            newValues.put("DripperSpacing", dripperSpacing);
            newValues.put("DischargeRate", dischargeRate);
            newValues.put("OwnerName", ownerName);
            newValues.put("OwnerMobile", ownerMobile);
            newValues.put("IsSync", "1");
            newValues.put("JobCardAllowed", jobCardAllowed);

            db = dbHelper.getWritableDatabase();
            db.insert("FarmBlock", null, newValues);
        } else {
            selectQuery = "UPDATE FarmBlock SET LandTypeId ='" + landTypeId + "',OwnerName ='" + ownerName + "',OwnerMobile ='" + ownerMobile + "',FPOId ='" + fPOId + "', KhataNo='" + khataNo + "', KhasraNo='" + khasraNo + "', ContractDate='" + contractDate + "', Acerage='" + acerage + "',SoilTypeId ='" + soilTypeId + "', ElevationMSL='" + elevationMSL + "', PHChemical='" + pHChemical + "', Nitrogen='" + nitrogen + "', Potash='" + potash + "', Phosphorus='" + phosphorus + "', OrganicCarbonPerc='" + organicCarbonPerc + "', Magnesium='" + magnesium + "', Calcium='" + calcium + "',ExistingUseId ='" + existingUseId + "', CommunityUseId='" + communityUseId + "', ExistingHazardId='" + existingHazardId + "', RiverId='" + riverId + "', DamId='" + damId + "', IrrigationId='" + irrigationId + "', OverheadTransmission='" + overheadTransmission + "', LegalDisputeId='" + legalDisputeId + "', SourceWaterId='" + sourceWaterId + "', ElectricitySourceId='" + electricitySourceId + "', DripperSpacing='" + dripperSpacing + "', DischargeRate='" + dischargeRate + "',IsSync='1',JobCardAllowed= '" + jobCardAllowed + "' WHERE FarmBlockUniqueId = '" + uniqueId + "' ";
            db.execSQL(selectQuery);
        }

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farm Cropping Pattern from Server in FarmBlockCroppingPattern Table">
    public String insertUpdateServerFarmCroppings(String uniqueId, String cropId, String cropVarietyId, String seasonId, String acreage, String flag) {
        result = "fail";
        if (flag.equalsIgnoreCase("Yes")) {
            selectQuery = "DELETE FROM  FarmBlockCroppingPattern WHERE FarmBlockUniqueId ='" + uniqueId + "' ";
            db.execSQL(selectQuery);
        }
        prevfarmCroppingUniqueId = uniqueId;
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmBlockUniqueId", uniqueId);
        newValues.put("CropId", cropId);
        newValues.put("CropVarietyId", cropVarietyId);
        newValues.put("SeasonId", seasonId);
        newValues.put("Acreage", acreage);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmBlockCroppingPattern", null, newValues);

        result = "success";
        return result;
    }

    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farm Coordinates from Server in FarmBlockCoordinates Table">
    public String insertUpdateServerFarmCoordinates(String uniqueId, String latitude, String longitude, String accuracy, String flag) {
        result = "fail";
        if (flag.equalsIgnoreCase("Yes")) {
            selectQuery = "DELETE FROM  FarmBlockCoordinates WHERE FarmBlockUniqueId ='" + uniqueId + "' ";
            db.execSQL(selectQuery);
        }
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmBlockUniqueId", uniqueId);
        newValues.put("Latitude", latitude);
        newValues.put("Longitude", longitude);
        newValues.put("Accuracy", accuracy);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmBlockCoordinates", null, newValues);

        result = "success";
        return result;
    }

    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farm Characteristic from Server in FarmBlockLandCharacteristic Table">
    public String insertUpdateServerFarmCharacteristic(String uniqueId, String landCharacteristicId, String flag) {
        result = "fail";
        if (flag.equalsIgnoreCase("Yes")) {
            selectQuery = "DELETE FROM  FarmBlockLandCharacteristic WHERE FarmBlockUniqueId ='" + uniqueId + "' ";
            db.execSQL(selectQuery);
        }
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmBlockUniqueId", uniqueId);
        newValues.put("LandCharacteristicId", landCharacteristicId);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmBlockLandCharacteristic", null, newValues);

        result = "success";
        return result;
    }

    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farm Issues from Server in FarmBlockLandIssue Table">
    public String insertUpdateServerFarmIssues(String uniqueId, String landIssueId, String flag) {
        result = "fail";
        if (flag.equalsIgnoreCase("Yes")) {
            selectQuery = "DELETE FROM  FarmBlockLandIssue WHERE FarmBlockUniqueId ='" + uniqueId + "' ";
            db.execSQL(selectQuery);
        }
        result = "fail";
        newValues = new ContentValues();

        newValues.put("FarmBlockUniqueId", uniqueId);
        newValues.put("landIssueId", landIssueId);

        db = dbHelper.getWritableDatabase();
        db.insert("FarmBlockLandIssue", null, newValues);

        result = "success";
        return result;
    }

    //</editor-fold>

    //<editor-fold desc="Method to Insert Plantation details in FarmerPlantation table">
    public String insertPlantationFromServer(String uniqueId, String farmBlockUniqueId, String plantTypeId, String zoneId, String cropVarietyId, String monthAgeId, String acreage, String plantationDate, String plantingSystemId, String plantRow, String plantColumn, String balance, String totalPlant, String plantationCode, String cropId) {
        result = "fail";
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM FarmerPlantation WHERE PlantationUniqueId = '" + uniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);
        newValues = new ContentValues();
        newValues.put("PlantationUniqueId", uniqueId);
        db = dbHelper.getWritableDatabase();
        db.insert("PlantationSyncTable", null, newValues);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        if (dataExists.equals(false)) {
            newValues = new ContentValues();

            newValues.put("PlantationUniqueId", uniqueId);
            newValues.put("FarmerUniqueId", "");
            newValues.put("FarmBlockUniqueId", farmBlockUniqueId);
            newValues.put("ZoneId", zoneId);
            newValues.put("CropId", cropId);
            newValues.put("CropVarietyId", cropVarietyId);
            newValues.put("PlantTypeId", plantTypeId);
            newValues.put("MonthAgeId", monthAgeId);
            newValues.put("Acreage", acreage);
            newValues.put("PlantationDate", plantationDate);
            newValues.put("PlantingSystemId", plantingSystemId);
            newValues.put("PlantRow", plantRow);
            newValues.put("PlantColumn", plantColumn);
            newValues.put("Balance", balance);
            newValues.put("TotalPlant", totalPlant);
            newValues.put("IsSync", "1");
            newValues.put("PlantationCode", plantationCode);
            newValues.put("CreateDate", getDateTime());
            db = dbHelper.getWritableDatabase();
            db.insert("FarmerPlantation", null, newValues);
        } else {
            selectQuery = "UPDATE FarmerPlantation SET PlantationCode ='" + plantationCode + "',FarmBlockUniqueId ='" + farmBlockUniqueId + "', ZoneId='" + zoneId + "', CropId='" + cropId + "', CropVarietyId='" + cropVarietyId + "', Acreage='" + acreage + "',PlantTypeId ='" + plantTypeId + "', MonthAgeId='" + monthAgeId + "', PlantationDate='" + plantationDate + "', PlantingSystemId='" + plantingSystemId + "', PlantRow='" + plantRow + "', PlantColumn='" + plantColumn + "', Balance='" + balance + "', TotalPlant='" + totalPlant + "', IsSync='1' WHERE PlantationUniqueId = '" + uniqueId + "' ";
            db.execSQL(selectQuery);
        }

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert detail from Server in InterCropping table">
    public String insertServerInterCropping(String uniqueId, String farmBlockPlantationId, String cropVarietyId, String seasonId, String acreage, String finYear) {
        result = "fail";
        Boolean dataExists = false;
        newValues = new ContentValues();
        newValues.put("InterCroppingUniqueId", uniqueId);
        db = dbHelper.getWritableDatabase();
        db.insert("InterCroppingSyncTable", null, newValues);
        selectQuery = "SELECT Id FROM InterCropping WHERE InterCroppingUniqueId = '" + uniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        if (dataExists.equals(false)) {
            cursor.close();
            newValues = new ContentValues();

            newValues.put("InterCroppingUniqueId", uniqueId);
            newValues.put("FarmerPlantationUniqueId", farmBlockPlantationId);
            newValues.put("CropVarietyId", cropVarietyId);
            newValues.put("SeasonId", seasonId);
            newValues.put("Acreage", acreage);
            newValues.put("FinancialYear", finYear);
            newValues.put("CreateDate", getDateTime());
            newValues.put("IsSync", "1");

            db = dbHelper.getWritableDatabase();
            db.insert("InterCropping", null, newValues);
        } else {
            selectQuery = "UPDATE InterCropping SET CropVarietyId ='" + cropVarietyId + "',SeasonId ='" + seasonId + "', Acreage='" + acreage + "',FinancialYear='" + finYear + "', IsSync='1' WHERE InterCroppingUniqueId = '" + uniqueId + "' ";
            db.execSQL(selectQuery);
        }
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert detail from Server in NurseryInterCropping table">
    public String insertServerNurseryInterCropping(String uniqueId, String plantationId, String cropVarietyId,
                                                   String seasonId, String acreage, String finYear) {
        result = "fail";
        Boolean dataExists = false;
        newValues = new ContentValues();
        newValues.put("InterCroppingUniqueId", uniqueId);

        db = dbHelper.getWritableDatabase();
        db.insert("NurseryInterCroppingSyncTable", null, newValues);

        selectQuery = "SELECT Id FROM NurseryInterCropping WHERE InterCroppingUniqueId = '" + uniqueId + "'";

        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        if (dataExists.equals(false)) {
            cursor.close();
            newValues = new ContentValues();

            newValues.put("InterCroppingUniqueId", uniqueId);
            newValues.put("PlantationUniqueId", plantationId);
            newValues.put("CropVarietyId", cropVarietyId);
            newValues.put("SeasonId", seasonId);
            newValues.put("Acreage", acreage);
            newValues.put("FinancialYear", finYear);
            newValues.put("CreateDate", getDateTime());
            newValues.put("IsSync", "1");

            db = dbHelper.getWritableDatabase();
            db.insert("NurseryInterCropping", null, newValues);
        } else {
            selectQuery = "UPDATE NurseryInterCropping " +
                    "SET CropVarietyId ='" + cropVarietyId + "'," +
                    "SeasonId ='" + seasonId + "', " +
                    "Acreage='" + acreage + "'," +
                    "FinancialYear='" + finYear + "', " +
                    "IsSync='1' " +
                    "WHERE InterCroppingUniqueId = '" + uniqueId + "' ";
            db.execSQL(selectQuery);
        }
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert data in VisitReport Table">
    public String InsertVisitReport(String visitUniqueId, String farmerUniqueId, String farmBlockNurseryUniqueId, String farmBlockNurseryType, String zoneId, String plantationId, String plantHeight, String plantStatusId, String days, String latitude, String longitude, String accuracy, String createBy) {
        try {
            result = "fail";
            newValues = new ContentValues();
            newValues.put("VisitUniqueId", visitUniqueId);
            newValues.put("FarmerUniqueId", farmerUniqueId);
            newValues.put("FarmBlockNurseryUniqueId", farmBlockNurseryUniqueId);
            newValues.put("FarmBlockNurseryType", farmBlockNurseryType);
            newValues.put("ZoneId", zoneId);
            newValues.put("PlantationId", plantationId);
            newValues.put("PlantHeight", plantHeight);
            newValues.put("PlantStatusId", plantStatusId);
            newValues.put("Days", days);
            newValues.put("Latitude", latitude);
            newValues.put("Longitude", longitude);
            newValues.put("Accuracy", accuracy);
            newValues.put("CreateBy", createBy);
            newValues.put("CreateDate", getDateTime());
            newValues.put("IsTemp", "1");

            db.insert("VisitReport", null, newValues);
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to update data in VisitReport Table">
    public String UpdateVisitReport(String visitUniqueId, String farmerUniqueId, String farmBlockNurseryUniqueId, String farmBlockNurseryType, String zoneId, String plantationId, String plantHeight, String plantStatusId, String days, String latitude, String longitude, String accuracy) {
        try {
            result = "fail";
            selectQuery = "UPDATE VisitReport SET FarmerUniqueId= '" + farmerUniqueId + "',FarmBlockNurseryUniqueId= '" + farmBlockNurseryUniqueId + "',FarmBlockNurseryType= '" + farmBlockNurseryType + "',ZoneId= '" + zoneId + "',PlantationId= '" + plantationId + "',PlantHeight= '" + plantHeight + "',PlantStatusId= '" + plantStatusId + "',Days= '" + days + "', Latitude= '" + latitude + "',Longitude= '" + longitude + "',Accuracy= '" + accuracy + "' WHERE VisitUniqueId ='" + visitUniqueId + "' ";
            db.execSQL(selectQuery);
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to update IsTemp flag in VisitReport, VisitReportDetail and VisitReportPhoto Table">
    public String UpdateSaveFlagVisitReport(String visitUniqueId) {
        try {
            result = "fail";
            db.execSQL("UPDATE VisitReport SET IsTemp = '0' WHERE VisitUniqueId ='" + visitUniqueId + "'");
            db.execSQL("UPDATE VisitReportDetail SET IsTemp = '0' WHERE VisitUniqueId ='" + visitUniqueId + "'");
            db.execSQL("UPDATE VisitReportPhoto SET IsTemp = '0' WHERE VisitUniqueId ='" + visitUniqueId + "'");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Planned Activity Details in JobCardDetailTemp Table">
    public String insertFarmPlannedDetailsTemp(String farmActivityId, String farmSubActivityId, String activityValue, String activityType, String plannerDetailId, String parameterDetailId, String uniqueId, String fileName) {
        result = "fail";

        Boolean dataExists = false;
        selectQuery = "SELECT ActivityType FROM JobCardDetailTemp WHERE FarmActivityId ='" + farmActivityId + "' AND FarmSubActivityId='" + farmSubActivityId + "'  AND ActivityType ='" + activityType + "'   ";

        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        if (!dataExists) {
            newValues = new ContentValues();

            newValues.put("FarmActivityId", farmActivityId);
            newValues.put("FarmSubActivityId", farmSubActivityId);
            newValues.put("ActivityValue", activityValue);
            newValues.put("ActivityType", activityType);
            newValues.put("PlannerDetailId", plannerDetailId);
            newValues.put("ParameterDetailId", parameterDetailId);
            newValues.put("UniqueId", uniqueId);
            newValues.put("FileName", fileName);

            db = dbHelper.getWritableDatabase();
            db.insert("JobCardDetailTemp", null, newValues);
        } else
            db.execSQL("UPDATE JobCardDetailTemp SET ActivityValue = '" + activityValue + "' WHERE FarmActivityId ='" + farmActivityId + "' AND FarmSubActivityId='" + farmSubActivityId + "'  AND ActivityType ='" + activityType + "' ");
        result = "success";
        return result;
    }
    //</editor-fold>

    public String insertDeliveryDetailsForDispatch(String dispatchId, String bookingId, String dispatchItemId, String quantity) {
        try {
            result = "fail";
            newValues = new ContentValues();

            newValues.put("DispatchId", dispatchId);
            newValues.put("BookingId", bookingId);
            newValues.put("DispatchItemId", dispatchItemId);
            newValues.put("Quantity", quantity);

            db.insert("DeliveryDetailsForDispatch", null, newValues);
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    public String insertPaymentDetailsPendingDispatchDelivery(String dispatchId, String bookingId, String totalAmount, String totalBalance,
                                                              String paymentMode, String paymentAmount, String paymentRemarks) {
        try {
            result = "fail";
            newValues = new ContentValues();

            newValues.put("DispatchId", dispatchId);
            newValues.put("BookingId", bookingId);
            newValues.put("TotalAmount", totalAmount);
            newValues.put("TotalBalance", totalBalance);
            newValues.put("PaymentMode", paymentMode);
            newValues.put("PaymentAmount", paymentAmount);
            newValues.put("PaymentRemarks", paymentRemarks);

            db.insert("PaymentAgainstDispatchDelivery", null, newValues);
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    public String updatePendingDispatchForDeliveryShortCloseReason(String dispatchId, String shortCloseReasonId) {
        try {
            result = "fail";
            selectQuery = "UPDATE PendingDispatchForDelivery " +
                    "SET ShortCloseReasonId = '" + shortCloseReasonId + "' " +
                    "WHERE Id ='" + dispatchId + "' ";
            db.execSQL(selectQuery);
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    //<editor-fold desc="Method to insert Temp Job Card File Data in temporary Table">
    public String Insert_TempJobCardFile(String activityId, String subActivityId, String type, String fileName) {
        try {
            result = "fail";
            newValues = new ContentValues();

            newValues.put("ActivityId", activityId);
            newValues.put("SubActivityId", subActivityId);
            newValues.put("Type", type);
            newValues.put("FileName", fileName);

            db.insert("TempJobCardFile", null, newValues);
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Data in Job Card and Job Card Detail table from Temporary Tables">
    public String InsertUpdate_JobCard(String jobCardUniqueId, String fbNurseryType, String fbNurseryUniqueId, String zoneId, String plUniqueId, String weekNo, String createBy, String longitude, String latitude, String accuracy, String createByRole) {
        try {


            result = "fail";
            Boolean dataExists = false;
            selectQuery = "SELECT Id FROM JobCard WHERE JobCardUniqueId = '" + jobCardUniqueId + "'";
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                dataExists = true;
            }
            cursor.close();
            if (dataExists.equals(false)) {
                newValues = new ContentValues();

                newValues.put("JobCardUniqueId", jobCardUniqueId);
                newValues.put("FarmBlockNurseryType", fbNurseryType);
                newValues.put("FarmBlockNurseryUniqueId", fbNurseryUniqueId);
                newValues.put("ZoneId", zoneId);
                newValues.put("PlantationUniqueId", plUniqueId);
                newValues.put("WeekNo", weekNo);
                newValues.put("VisitDate", getDateTime());
                newValues.put("Latitude", latitude);
                newValues.put("Longitude", longitude);
                newValues.put("Accuracy", accuracy);
                newValues.put("CreatBy", createBy);
                newValues.put("CreateDate", getDateTime());
                newValues.put("CreateByRole", createByRole);

                db.insert("JobCard", null, newValues);
            } else {
                selectQuery = "UPDATE JobCard SET VisitDate ='" + getDateTime() + "',Latitude ='" + latitude + "',Longitude ='" + longitude + "',Accuracy ='" + accuracy + "',CreateByRole='" + createByRole + "', CreatBy='" + createBy + "', CreateDate='" + getDateTime() + "',IsSync='' WHERE JobCardUniqueId = '" + jobCardUniqueId + "' ";
                db.execSQL(selectQuery);
            }
            selectQuery = "DELETE FROM JobCardDetail WHERE  JobCardUniqueId = '" + jobCardUniqueId + "' ";
            db.execSQL(selectQuery);

            selectQuery = "INSERT INTO JobCardDetail(JobCardUniqueId,FarmActivityId,FarmSubActivityId,ActivityValue,ActivtyType,PlannerDetailId, ParameterDetailId, UniqueId, FileName) SELECT '" + jobCardUniqueId + "',jt.FarmActivityId, jt.FarmSubActivityId, jt.ActivityValue, jt.ActivityType, jt.PlannerDetailId,jt.ParameterDetailId, jt.UniqueId,ifnull(jf.FileName,'') FROM JobCardDetailTemp jt LEFT OUTER JOIN TempJobCardFile jf ON jt.FarmActivityId = jf.ActivityId AND jt.FarmSubActivityId = jf.SubActivityId AND jt.ActivityType = SUBSTR(Type,1,1) ";
            db.execSQL(selectQuery);
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Data in Job Card Detail Temporary Tables From JobCardDetail By Job Card Unique Id">
    public String InsertMainToTempJobCard(String jobCardUniqueId) {
        try {


            result = "fail";
            selectQuery = "DELETE FROM JobCardDetailTemp ";
            db.execSQL(selectQuery);

            selectQuery = "DELETE FROM TempJobCardFile ";
            db.execSQL(selectQuery);

            selectQuery = "INSERT INTO JobCardDetailTemp(FarmActivityId,FarmSubActivityId,ActivityValue,ActivityType,PreviousValue,PlannerDetailId,ParameterDetailId,UniqueId,FileName) SELECT jd.FarmActivityId,jd.FarmSubActivityId,jd.ActivityValue,jd.ActivtyType,jd.ActivityValue,jd.PlannerDetailId,jd.ParameterDetailId, jd.UniqueId,jd.FileName FROM JobCardDetail jd, JobCard jc WHERE jc.JobCardUniqueId = jd.JobCardUniqueId AND SUBSTR(jc.VisitDate,9,2) =SUBSTR(DATE('now'),9,2) AND SUBSTR(jc.VisitDate,6,2) =SUBSTR(DATE('now'),6,2) AND SUBSTR(jc.VisitDate,1,4) =SUBSTR(DATE('now'),1,4) AND jd.JobCardUniqueId ='" + jobCardUniqueId + "' ";
            db.execSQL(selectQuery);

            selectQuery = "INSERT INTO TempJobCardFile(ActivityId,SubActivityId,Type,FileName) SELECT FarmActivityId, FarmSubActivityId, (CASE WHEN ActivtyType='P' THEN 'Planned' WHEN ActivtyType='R' THEN 'Recommended' ELSE 'Additional' END), FileName  FROM JobCardDetail WHERE FileName IS NOT NULL AND FileName !='' AND JobCardUniqueId ='" + jobCardUniqueId + "' ";
            db.execSQL(selectQuery);

            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="To Update IsSync flag">
    public String UpdateVisitReportIsSync() {
        try {
            db.execSQL("UPDATE VisitReport SET IsSync = '1' WHERE IsSync IS NULL AND IsTemp = '0'");
            db.execSQL("UPDATE VisitReportDetail SET IsSync = '1' WHERE IsSync IS NULL AND IsTemp = '0'");
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Nursery Table">
    public String insertNursery(String id, String uniqueId, String nurseryType, String nurseryTypeHindi, String title,
                                String titleHindi, String Salutation, String FirstName, String MiddleName, String LastName, String Mobile, String AlternateMobile, String EmailId, String LandType, String AddressUniqueId, String RegistryDate, String OfficePrimise, String GSTNo, String GSTDate, String OwnerName, String LoginId, String ContactNo, String MainNurseryId, String CertifiedBy, String RegistrationNo, String RegistrationDate, String KhataNo, String KhasraNo, String ContractDate, String Area, String SoilTypeId, String ElevationMSL, String SoilPH, String Nitrogen, String Potash, String Phosphorus, String OrganicCarbonPerc, String Magnesium, String Calcium, String ExistingUseId, String CommunityUseId, String ExistingHazardId, String RiverId, String DamId, String IrrigationId, String OverheadTransmission, String LegalDisputeId, String SourceWaterId, String ElectricitySourceId, String DripperSpacing, String DischargeRate) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("UniqueId", uniqueId);
        newValues.put("NurseryType", nurseryType);
        newValues.put("NurseryTypeHindi", nurseryTypeHindi);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);
        newValues.put("Salutation", Salutation);
        newValues.put("FirstName", FirstName);
        newValues.put("MiddleName", MiddleName);
        newValues.put("LastName", LastName);
        newValues.put("Mobile", Mobile);
        newValues.put("AlternateMobile", AlternateMobile);
        newValues.put("EmailId", EmailId);
        newValues.put("LandType", LandType);
        newValues.put("AddressUniqueId", AddressUniqueId);
        newValues.put("RegistryDate", RegistryDate);
        newValues.put("OfficePrimise", OfficePrimise);
        newValues.put("GSTNo", GSTNo);
        newValues.put("GSTDate", GSTDate);
        newValues.put("OwnerName", OwnerName);
        newValues.put("LoginId", LoginId);
        newValues.put("ContactNo", ContactNo);
        newValues.put("MainNurseryId", MainNurseryId);
        newValues.put("CertifiedBy", CertifiedBy);
        newValues.put("RegistrationNo", RegistrationNo);
        newValues.put("RegistrationDate", RegistrationDate);
        newValues.put("KhataNo", KhataNo);
        newValues.put("KhasraNo", KhasraNo);
        newValues.put("ContractDate", ContractDate);
        newValues.put("Area", Area);
        newValues.put("SoilTypeId", SoilTypeId);
        newValues.put("ElevationMSL", ElevationMSL);
        newValues.put("SoilPH", SoilPH);
        newValues.put("Nitrogen", Nitrogen);
        newValues.put("Potash", Phosphorus);
        newValues.put("OrganicCarbonPerc", OrganicCarbonPerc);
        newValues.put("Magnesium", Magnesium);
        newValues.put("Calcium", Calcium);
        newValues.put("ExistingUseId", ExistingUseId);
        newValues.put("CommunityUseId", CommunityUseId);
        newValues.put("ExistingHazardId", ExistingHazardId);
        newValues.put("RiverId", RiverId);
        newValues.put("DamId", DamId);
        newValues.put("IrrigationId", IrrigationId);
        newValues.put("OverheadTransmission", OverheadTransmission);
        newValues.put("LegalDisputeId", LegalDisputeId);
        newValues.put("SourceWaterId", SourceWaterId);
        newValues.put("ElectricitySourceId", ElectricitySourceId);
        newValues.put("DripperSpacing", DripperSpacing);
        newValues.put("DischargeRate", DischargeRate);
        db = dbHelper.getWritableDatabase();
        db.insert("Nursery", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Nursery Zone Table">
    public String insertNurseryZone(String id, String uniqueId, String nurseryId, String title, String titleHindi, String area) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", id);
        newValues.put("UniqueId", uniqueId);
        newValues.put("NurseryId", nurseryId);
        newValues.put("Title", title);
        newValues.put("TitleHindi", titleHindi);
        newValues.put("Area", area);
        db = dbHelper.getWritableDatabase();
        db.insert("NurseryZone", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Nursery Account Detail Table">
    public String insertNurseryAccountDetail(String Id, String NurseryId, String AccountNo, String IFSCCode) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", Id);
        newValues.put("NurseryId", NurseryId);
        newValues.put("AccountNo", AccountNo);
        newValues.put("IFSCCode", IFSCCode);
        db = dbHelper.getWritableDatabase();
        db.insert("NurseryAccountDetail", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Nursery LandCharacteristic Table">
    public String insertNurseryLandCharacteristic(String Id, String NurseryId, String LandCharacteristicId) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", Id);
        newValues.put("NurseryId", NurseryId);
        newValues.put("LandCharacteristicId", LandCharacteristicId);
        db = dbHelper.getWritableDatabase();
        db.insert("NurseryLandCharacteristic", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Nursery Land Issue Table">
    public String insertNurseryLandIssue(String Id, String NurseryId, String LandIssueId) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", Id);
        newValues.put("NurseryId", NurseryId);
        newValues.put("LandIssueId", LandIssueId);
        db = dbHelper.getWritableDatabase();
        db.insert("NurseryLandIssue", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Nursery Cropping Pattern Table">
    public String insertNurseryCroppingPattern(String Id, String NurseryId, String CropVarietyId, String Acreage, String SeasonId, String FinancialYear) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("Id", Id);
        newValues.put("NurseryId", NurseryId);
        newValues.put("CropVarietyId", CropVarietyId);
        newValues.put("Acreage", Acreage);
        newValues.put("SeasonId", SeasonId);
        newValues.put("FinancialYear", FinancialYear);
        db = dbHelper.getWritableDatabase();
        db.insert("NurseryCroppingPattern", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert coordinates in Temporary Nursery GPS Table">
    public String Insert_TempNurseryGPS(String latitude, String longitude, String accuracy) {
        try {
            result = "fail";
            newValues = new ContentValues();
            newValues.put("Latitude", latitude);
            newValues.put("Longitude", longitude);
            newValues.put("Accuracy", accuracy);
            db.insert("TempNurseryGPS", null, newValues);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold

    //<editor-fold desc="Method to insert details in FarmBlockCoordinates Table">
    public String insertNurseryZoneCoordinates(String nurseryId, String nurseryZoneId, String createBy) {
        result = "fail";
        String uniqueId = "";
        if (nurseryZoneId.equalsIgnoreCase("0"))
            selectQuery = "SELECT UniqueId FROM Nursery WHERE Id  = '" + nurseryId + "' ";
        else
            selectQuery = "SELECT UniqueId FROM NurseryZone WHERE Id = '" + nurseryZoneId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                uniqueId = String.valueOf(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.execSQL("INSERT INTO NurseryCoordinates(UniqueId, NurseryId, NurseryZoneId, Latitude, Longitude, Accuracy, CreateBy, CreateDate,  IsSync) SELECT '" + uniqueId + "','" + nurseryId + "', '" + nurseryZoneId + "', Latitude, Longitude, Accuracy, '" + createBy + "', '" + getDateTime() + "', 0  FROM TempNurseryGPS");
        db.execSQL("DELETE FROM TempNurseryGPS");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Update Nursery Coordinates from Server in NurseryCoordinates Table">
    public String insertUpdateServerNurseryCoordinates(String uniqueId, String nurseryId, String nurseryZoneId, String latitude, String longitude, String accuracy, String flag, String userId) {
        result = "fail";
        if (flag.equalsIgnoreCase("Yes")) {
            selectQuery = "DELETE FROM  NurseryCoordinates WHERE UniqueId ='" + uniqueId + "' ";
            db.execSQL(selectQuery);
        }
        result = "fail";
        newValues = new ContentValues();

        newValues.put("UniqueId", uniqueId);
        newValues.put("NurseryId", nurseryId);
        newValues.put("nurseryZoneId", nurseryZoneId);
        newValues.put("Latitude", latitude);
        newValues.put("Longitude", longitude);
        newValues.put("Accuracy", accuracy);
        newValues.put("CreateBy", userId);
        newValues.put("CreateDate", getDateTime());
        newValues.put("IsSync", "1");

        db = dbHelper.getWritableDatabase();
        db.insert("NurseryCoordinates", null, newValues);

        result = "success";
        return result;
    }

    //</editor-fold>

    //<editor-fold desc="To Update New NurseryCoordinates IsSync flag">
    public String Update_NZCIsSync() {
        try {
            String query = "UPDATE NurseryCoordinates SET IsSync = '1' WHERE IsSync = 0 ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="To Update Job Card IsSync flag">
    public String Update_JobCardIsSync() {
        try {
            String query = "UPDATE JobCard SET IsSync = '1' WHERE  IsSync IS NULL OR IsSync ='' ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in PlantationWeek Table">
    public String insertPlantationWeek(String plantationUniqueId, String weekNo, String fromDate, String toDate) {
        result = "fail";
        newValues = new ContentValues();

        newValues.put("PlantationUniqueId", plantationUniqueId);
        newValues.put("WeekNo", weekNo);
        newValues.put("FromDate", fromDate);
        newValues.put("ToDate", toDate);

        db = dbHelper.getWritableDatabase();
        db.insert("PlantationWeek", null, newValues);
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to Insert Nursery Plantation details in NurseryPlantation table">
    public String insertNurseryPlantationFromServer(String uniqueId, String nurseryUniqueId, String nurseryId, String plantTypeId,
                                                    String zoneId, String cropVarietyId, String monthAgeId, String acreage,
                                                    String plantationDate, String plantingSystemId, String plantRow, String plantColumn,
                                                    String balance, String totalPlant, String plantationCode, String cropId) {
        result = "fail";
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM NurseryPlantation WHERE PlantationUniqueId = '" + uniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);
        newValues = new ContentValues();
        newValues.put("PlantationUniqueId", uniqueId);
        db = dbHelper.getWritableDatabase();
        db.insert("PlantationSyncTable", null, newValues);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        if (dataExists.equals(false)) {
            newValues = new ContentValues();

            newValues.put("PlantationUniqueId", uniqueId);
            newValues.put("NurseryUniqueId", nurseryUniqueId);
            newValues.put("NurseryId", nurseryId);
            newValues.put("ZoneId", zoneId);
            newValues.put("CropId", cropId);
            newValues.put("CropVarietyId", cropVarietyId);
            newValues.put("PlantTypeId", plantTypeId);
            newValues.put("MonthAgeId", monthAgeId);
            newValues.put("Acreage", acreage);
            newValues.put("PlantationDate", plantationDate);
            newValues.put("PlantingSystemId", plantingSystemId);
            newValues.put("PlantRow", plantRow);
            newValues.put("PlantColumn", plantColumn);
            newValues.put("Balance", balance);
            newValues.put("TotalPlant", totalPlant);
            newValues.put("IsSync", "1");
            newValues.put("PlantationCode", plantationCode);
            newValues.put("CreateDate", getDateTime());
            db = dbHelper.getWritableDatabase();
            db.insert("NurseryPlantation", null, newValues);
        } else {
            selectQuery = "UPDATE NurseryPlantation SET PlantationCode ='" + plantationCode + "', " +
                    "NurseryUniqueId = '" + nurseryUniqueId + "', NurseryId ='" + nurseryId + "', ZoneId='" + zoneId + "', CropId='" + cropId + "', " +
                    "CropVarietyId='" + cropVarietyId + "', Acreage='" + acreage + "',PlantTypeId ='" + plantTypeId + "', " +
                    "MonthAgeId='" + monthAgeId + "', PlantationDate='" + plantationDate + "', " +
                    "PlantingSystemId='" + plantingSystemId + "', PlantRow='" + plantRow + "', PlantColumn='" + plantColumn + "', " +
                    "Balance='" + balance + "', TotalPlant='" + totalPlant + "', IsSync='1' WHERE PlantationUniqueId = '" + uniqueId + "' ";
            db.execSQL(selectQuery);
        }

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to Update Plantation Code">
    public String updateNurseryPlantationCode(String plantationUniqueId, String plantationCode) {
        // TODO - check if any table needs to be updated
        result = "fail";
        selectQuery = "UPDATE NurseryPlantation SET PlantationCode= '" + plantationCode + "' WHERE PlantationUniqueId ='" + plantationUniqueId + "' ";
        db.execSQL(selectQuery);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="To get all PlantationUniqueId for Inserting Into Nursery Plantation Sync Table">
    public ArrayList<HashMap<String, String>> getNurseryPlantationUniqueUpdateId() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT PlantationUniqueId FROM NurseryPlantationSyncTable";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("PlantationUniqueId", cursor.getString(0));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert data in VisitReportDetail Table">
    public String InsertVisitReportDetail(String visitUniqueId, String defectId, String remarks, String fileName) {
        try {
            result = "fail";
            String uniqueId = UUID.randomUUID().toString();
            newValues = new ContentValues();
            newValues.put("VisitUniqueId", visitUniqueId);
            newValues.put("UniqueId", uniqueId);
            newValues.put("DefectId", defectId);
            newValues.put("Remarks", remarks);
            newValues.put("IsTemp", "1");
            db.insert("VisitReportDetail", null, newValues);

            if (!fileName.equalsIgnoreCase("")) {
                db.execSQL("DELETE FROM VisitReportPhoto WHERE VisitUniqueId = '" + visitUniqueId + "' AND DefectId = '" + defectId + "'; ");
                db.execSQL("INSERT INTO VisitReportPhoto(VisitUniqueId, UniqueId, DefectId, FileName, FilePath, IsTemp) SELECT '" + visitUniqueId + "','" + uniqueId + "','" + defectId + "','" + fileName + "', FileName, '1' FROM TempFile ");
                db.execSQL("DELETE FROM TempFile; ");
            }
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to update IsTemp flag in Recommendation and RecommendationDetail Table">
    public String UpdateSaveFlagRecommendation(String uniqueId) {
        try {
            result = "fail";
            db.execSQL("UPDATE Recommendation SET IsTemp = '0' WHERE UniqueId ='" + uniqueId + "'");
            db.execSQL("UPDATE RecommendationDetail SET IsTemp = '0' WHERE RecommendationUniqueId ='" + uniqueId + "'");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert data in RecommendationDetail Table">
    public String InsertRecommendationDetail(String recommendationUniqueId, String farmActivityId, String farmSubActivityId, String uomId, String weekNo, String remarks, String activityValue, String fileName, String farmerUniqueId, String farmBlockNurseryUniqueId, String farmBlockNurseryType, String zoneId, String plantationId, String latitude, String longitude, String accuracy, String createBy, String jcWeek) {
        try {
            result = "fail";
            Boolean dataExists = false;
            selectQuery = "SELECT Id FROM Recommendation WHERE UniqueId = '" + recommendationUniqueId + "'";
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                dataExists = true;
            }
            cursor.close();
            if (dataExists.equals(false)) {
                newValues = new ContentValues();
                newValues.put("UniqueId", recommendationUniqueId);
                newValues.put("FarmerUniqueId", farmerUniqueId);
                newValues.put("FarmBlockNurseryUniqueId", farmBlockNurseryUniqueId);
                newValues.put("FarmBlockNurseryType", farmBlockNurseryType);
                newValues.put("ZoneId", zoneId);
                newValues.put("PlantationId", plantationId);
                newValues.put("Latitude", latitude);
                newValues.put("Longitude", longitude);
                newValues.put("Accuracy", accuracy);
                newValues.put("CreateBy", createBy);
                newValues.put("CreateDate", getDateTime());
                newValues.put("IsTemp", "1");
                db.insert("Recommendation", null, newValues);
            }

            String uniqueId = UUID.randomUUID().toString();
            newValues = new ContentValues();
            newValues.put("RecommendationUniqueId", recommendationUniqueId);
            newValues.put("UniqueId", uniqueId);
            newValues.put("FarmActivityId", farmActivityId);
            newValues.put("FarmSubActivityId", farmSubActivityId);
            newValues.put("UomId", uomId);
            newValues.put("WeekNo", weekNo);
            newValues.put("Remarks", remarks);
            newValues.put("ActivityValue", activityValue);
            newValues.put("FileName", fileName);
            newValues.put("IsTemp", "1");
            newValues.put("JCWeek", jcWeek);
            db.insert("RecommendationDetail", null, newValues);

            db.execSQL("DELETE FROM TempFile; ");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="To Update IsSync flag">
    public String UpdateRecommendationIsSync() {
        try {
            db.execSQL("UPDATE Recommendation SET IsSyncData = '1' WHERE IsSyncData IS NULL AND IsTemp = '0'");
            db.execSQL("UPDATE RecommendationDetail SET IsSyncData = '1' WHERE IsSyncData IS NULL AND IsTemp = '0'");
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Planned Activity Details in JobCardPending Table">
    public String updateJobCardPending(String uniqueId, String plUniqueId, String detailId, String cfValue) {
        try {
            result = "fail";
            db.execSQL("UPDATE JobCardPending SET IsSync = 0, CreateDate = '" + getDateTime() + "', ConfirmValue = '" + cfValue + "' WHERE JobCardDetailId ='" + detailId + "' AND UniqueId ='" + uniqueId + "' AND PlantationUniqueId ='" + plUniqueId + "' ");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert details in Pending Job Card Table">
    public String insertPendingJobCard(String jobCardId, String fbNurType, String fbNurId, String
            uniqueId, String zoneId, String plantationUniqueId, String visitDate, String createBy,
                                       String detailId, String farmActivityId, String farmSubActivityId, String activityValue,
                                       String activityType, String plannedValue, String remarks) {
        result = "fail";
        newValues = new ContentValues();
        newValues.put("JobCardId", jobCardId);
        newValues.put("FarmBlockNurseryType", fbNurType);
        newValues.put("FarmBlockNurseryId", fbNurId);
        newValues.put("UniqueId", uniqueId);
        newValues.put("ZoneId", zoneId);
        newValues.put("PlantationUniqueId", plantationUniqueId);
        newValues.put("VisitDate", visitDate);
        newValues.put("CreateBy", createBy);
        newValues.put("JobCardDetailId", detailId);
        newValues.put("FarmActivityId", farmActivityId);
        newValues.put("FarmSubActivityId", farmSubActivityId);
        newValues.put("ActivityValue", activityValue);
        newValues.put("ActivityType", activityType);
        newValues.put("PlannedValue", plannedValue);
        newValues.put("Remarks", remarks);
        db = dbHelper.getWritableDatabase();
        db.insert("JobCardPending", null, newValues);

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Data in Booking Collection From Server">
    public String InsertServerBookingCollections(String uniqueId, String bookingFor, String bookingForId, String farmBlockId, String street1, String street2, String stateId, String districtId, String blockId, String panchayatId, String villageId, String cityId, String pinCodeId, String addressType, String bookingDate, String deliveryDate, String quantity, String rate, String totalAmount, String paymentMode, String paymentAmount, String remarks, String fileName, String filePath, String balanceQuantity, String shortCloseQuantity, String shortCloseReasonId, String shortCloseBy, String shortCloseDate, String createBy, String createDate) {
        try {
            result = "fail";
            Boolean dataExists = false;
            selectQuery = "SELECT Id FROM Booking WHERE BookingUniqueId = '" + uniqueId + "'";
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                dataExists = true;
            }
            cursor.close();
            if (dataExists.equals(false)) {
                newValues = new ContentValues();
                newValues.put("BookingUniqueId", uniqueId);
                newValues.put("BookingFor", bookingFor);
                newValues.put("BookingForId", bookingForId);
                newValues.put("FarmBlockId", farmBlockId);
                newValues.put("Street1", street1);
                newValues.put("Street2", street2);
                newValues.put("StateId", stateId);
                newValues.put("DistrictId", districtId);
                newValues.put("BlockId", blockId);
                newValues.put("PanchayatId", panchayatId);
                newValues.put("VillageId", villageId);
                newValues.put("CityId", cityId);
                newValues.put("PinCodeId", pinCodeId);
                newValues.put("AddressType", addressType);
                newValues.put("BookingDate", bookingDate);
                newValues.put("DeliveryDate", deliveryDate);
                newValues.put("Quantity", quantity);
                newValues.put("Rate", rate);
                newValues.put("TotalAmount", totalAmount);
                newValues.put("PaymentMode", paymentMode);
                newValues.put("PaymentAmount", paymentAmount);
                newValues.put("Remarks", remarks);
                newValues.put("FileName", fileName);
                newValues.put("FilePath", filePath);
                newValues.put("BalanceQuantity", balanceQuantity);
                newValues.put("ShortCloseQuantity", shortCloseQuantity);
                newValues.put("ShortCloseReasonId", shortCloseReasonId);
                newValues.put("ShortCloseBy", shortCloseBy);
                newValues.put("ShortCloseDate", shortCloseDate);
                newValues.put("CreateBy", createBy);
                newValues.put("CreateDate", createDate);
                newValues.put("IsSync", "1");
                db.insert("Booking", null, newValues);
            }
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Data in Job Card From Server">
    public String InsertServerJobCards(String jobCardUniqueId, String fbNurseryType, String fbNurseryUniqueId, String zoneId, String plUniqueId, String weekNo, String visitDate) {
        try {


            result = "fail";
            Boolean dataExists = false;
            selectQuery = "SELECT Id FROM JobCard WHERE JobCardUniqueId = '" + jobCardUniqueId + "'";
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                dataExists = true;
            }
            cursor.close();
            if (dataExists.equals(false)) {
                newValues = new ContentValues();

                newValues.put("JobCardUniqueId", jobCardUniqueId);
                newValues.put("FarmBlockNurseryType", fbNurseryType);
                newValues.put("FarmBlockNurseryUniqueId", fbNurseryUniqueId);
                newValues.put("ZoneId", zoneId);
                newValues.put("PlantationUniqueId", plUniqueId);
                newValues.put("WeekNo", weekNo);
                newValues.put("VisitDate", visitDate);
                newValues.put("CreateDate", visitDate);
                newValues.put("IsSync", "1");

                db.insert("JobCard", null, newValues);
            }
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Data in Job Card Detail From Server">
    public String InsertServerJobCardDetails(String jobCardUniqueId, String farmActivityId, String farmSubActivityId, String activityValue, String activtyType, String plannerDetailId, String parameterDetailId, String uniqueId, String fileName) {
        try {


            result = "fail";

            newValues = new ContentValues();

            newValues.put("JobCardUniqueId", jobCardUniqueId);
            newValues.put("FarmActivityId", farmActivityId);
            newValues.put("FarmSubActivityId", farmSubActivityId);
            newValues.put("ActivityValue", activityValue);
            newValues.put("ActivtyType", activtyType);
            newValues.put("PlannerDetailId", plannerDetailId);
            newValues.put("ParameterDetailId", parameterDetailId);
            newValues.put("UniqueId", uniqueId);
            newValues.put("FileName", fileName);
            newValues.put("IsSync", "1");


            db.insert("JobCardDetail", null, newValues);
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>
    //--------------------------------------------End of Insert Queries---------------------------------------------//

    //--------------------------------------------Start of Delete Queries---------------------------------------------//

    //<editor-fold desc="Delete all masters data">
    public String deleteAllData() {
        result = "fail";
        try {
            db.execSQL("DELETE FROM UserRoles; ");
            db.execSQL("DELETE FROM State; ");
            db.execSQL("DELETE FROM District; ");
            db.execSQL("DELETE FROM City; ");
            db.execSQL("DELETE FROM Block; ");
            db.execSQL("DELETE FROM Panchayat; ");
            db.execSQL("DELETE FROM Village; ");
            db.execSQL("DELETE FROM PinCode; ");
            db.execSQL("DELETE FROM FarmerType; ");
            db.execSQL("DELETE FROM AddressTemp; ");
            db.execSQL("DELETE FROM EducationLevel; ");
            db.execSQL("DELETE FROM ProofType; ");
            db.execSQL("DELETE FROM POAPOI; ");
            db.execSQL("DELETE FROM Organizer; ");
            db.execSQL("DELETE FROM LandIssue; ");
            db.execSQL("DELETE FROM SoilType; ");
            db.execSQL("DELETE FROM LandType; ");
            db.execSQL("DELETE FROM ExistingUse; ");
            db.execSQL("DELETE FROM CommunityUse; ");
            db.execSQL("DELETE FROM ExistingHazard; ");
            db.execSQL("DELETE FROM NearestDam; ");
            db.execSQL("DELETE FROM NearestRiver; ");
            db.execSQL("DELETE FROM ElectricitySource; ");
            db.execSQL("DELETE FROM WaterSource; ");
            db.execSQL("DELETE FROM LoanSource; ");
            db.execSQL("DELETE FROM LoanType; ");
            db.execSQL("DELETE FROM FarmAsset; ");
            db.execSQL("DELETE FROM RelationShip; ");
            db.execSQL("DELETE FROM IrrigationSystem; ");
            db.execSQL("DELETE FROM LandCharacteristic; ");
            db.execSQL("DELETE FROM Season; ");
            db.execSQL("DELETE FROM Crop; ");
            db.execSQL("DELETE FROM LegalDispute; ");
            db.execSQL("DELETE FROM Variety; ");
            db.execSQL("DELETE FROM Monthage; ");
            db.execSQL("DELETE FROM TempFile; ");
            db.execSQL("DELETE FROM Languages; ");
            db.execSQL("DELETE FROM PlantType; ");
            db.execSQL("DELETE FROM PlantingSystem; ");
            db.execSQL("DELETE FROM UOM; ");
            db.execSQL("DELETE FROM FarmActivityType; ");
            db.execSQL("DELETE FROM FarmActivity; ");
            db.execSQL("DELETE FROM FarmSubActivity; ");
            db.execSQL("DELETE FROM PlannedActivity; ");
            db.execSQL("DELETE FROM AllActivity; ");
            db.execSQL("DELETE FROM PlantStatus; ");
            db.execSQL("DELETE FROM Defect; ");
            db.execSQL("DELETE FROM NurseryZone");
            db.execSQL("DELETE FROM NurseryCoordinates");
            db.execSQL("DELETE FROM Nursery; ");
            db.execSQL("DELETE FROM NurseryAccountDetail; ");
            db.execSQL("DELETE FROM NurseryLandCharacteristic; ");
            db.execSQL("DELETE FROM NurseryCroppingPattern; ");
            db.execSQL("DELETE FROM NurseryLandIssue; ");
            db.execSQL("DELETE FROM PlantationWeek; ");
            db.execSQL("DELETE FROM JobCardPending; ");
            db.execSQL("DELETE FROM PolyBagRate; ");
            db.execSQL("DELETE FROM PaymentMode; ");
            db.execSQL("DELETE FROM ShortCloseReason");
            db.execSQL("DELETE FROM BookingAddressTemp; ");
            result = "success";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Delete Job Card Detail From Temporary Table">
    public String deleteTempJobCardDetail() {
        result = "fail";
        try {
            db.execSQL("DELETE FROM JobCardDetailTemp; ");
            db.execSQL("DELETE FROM TempJobCardFile; ");

            result = "success";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
    //</editor-fold>


    //<editor-fold desc="Delete all Transaction data">
    public String deleteTransactionData() {
        try {
            db.execSQL("DELETE FROM FarmerSyncTable; ");
            db.execSQL("DELETE FROM FarmBlockSyncTable; ");
            db.execSQL("DELETE FROM PlantationSyncTable; ");
            db.execSQL("DELETE FROM PlantType; ");
            db.execSQL("DELETE FROM PlantingSystem; ");
            db.execSQL("DELETE FROM TempFarmerDocument; ");
            db.execSQL("DELETE FROM FarmerDocuments; ");
            db.execSQL("DELETE FROM FarmerProofTemp; ");
            db.execSQL("DELETE FROM FarmerOperatingBlocksTemp; ");
            db.execSQL("DELETE FROM FarmerOperatingBlocks; ");
            db.execSQL("DELETE FROM FarmerOtherDetailsTemp; ");
            db.execSQL("DELETE FROM FarmerOtherDetails; ");
            db.execSQL("DELETE FROM FarmBlockCoordinates; ");
            db.execSQL("DELETE FROM FarmBlockCroppingPattern; ");
            db.execSQL("DELETE FROM FarmBlockLandCharacteristic; ");
            db.execSQL("DELETE FROM FarmBlockLandIssue; ");
            db.execSQL("DELETE FROM FarmerPlantation; ");
            db.execSQL("DELETE FROM InterCropping ; ");
            db.execSQL("DELETE FROM TempGPS; ");
            db.execSQL("DELETE FROM FarmBlock; ");
            db.execSQL("DELETE FROM FarmerFamilyMemberTemp; ");
            db.execSQL("DELETE FROM FarmerFamilyMember; ");
            db.execSQL("DELETE FROM FarmerLoanDetailsTemp; ");
            db.execSQL("DELETE FROM FarmerLoanDetails; ");
            db.execSQL("DELETE FROM FarmerAssetDetails; ");
            db.execSQL("DELETE FROM Farmer; ");
            db.execSQL("DELETE FROM FarmerTemp; ");
            db.execSQL("DELETE FROM UserRoles; ");
            db.execSQL("DELETE FROM State; ");
            db.execSQL("DELETE FROM District; ");
            db.execSQL("DELETE FROM City; ");
            db.execSQL("DELETE FROM Block; ");
            db.execSQL("DELETE FROM Panchayat; ");
            db.execSQL("DELETE FROM Village; ");
            db.execSQL("DELETE FROM PinCode; ");
            db.execSQL("DELETE FROM FarmerType; ");
            db.execSQL("DELETE FROM Farmer; ");
            db.execSQL("DELETE FROM FarmerTemp; ");
            db.execSQL("DELETE FROM Address; ");
            db.execSQL("DELETE FROM FarmerPlantation; ");
            db.execSQL("DELETE FROM InterCropping ; ");
            db.execSQL("DELETE FROM AddressTemp; ");
            db.execSQL("DELETE FROM EducationLevel; ");
            db.execSQL("DELETE FROM ProofType; ");
            db.execSQL("DELETE FROM POAPOI; ");
            db.execSQL("DELETE FROM Organizer; ");
            db.execSQL("DELETE FROM LandIssue; ");
            db.execSQL("DELETE FROM SoilType; ");
            db.execSQL("DELETE FROM LandType; ");
            db.execSQL("DELETE FROM ExistingUse; ");
            db.execSQL("DELETE FROM CommunityUse; ");
            db.execSQL("DELETE FROM ExistingHazard; ");
            db.execSQL("DELETE FROM NearestDam; ");
            db.execSQL("DELETE FROM NearestRiver; ");
            db.execSQL("DELETE FROM ElectricitySource; ");
            db.execSQL("DELETE FROM WaterSource; ");
            db.execSQL("DELETE FROM LoanSource; ");
            db.execSQL("DELETE FROM LoanType; ");
            db.execSQL("DELETE FROM FarmAsset; ");
            db.execSQL("DELETE FROM RelationShip; ");
            db.execSQL("DELETE FROM IrrigationSystem; ");
            db.execSQL("DELETE FROM LandCharacteristic; ");
            db.execSQL("DELETE FROM Season; ");
            db.execSQL("DELETE FROM Crop; ");
            db.execSQL("DELETE FROM LegalDispute; ");
            db.execSQL("DELETE FROM Variety; ");
            db.execSQL("DELETE FROM Monthage; ");
            db.execSQL("DELETE FROM TempFile; ");
            db.execSQL("DELETE FROM FarmerProof; ");
            db.execSQL("DELETE FROM Languages; ");
            db.execSQL("DELETE FROM UOM; ");
            db.execSQL("DELETE FROM FarmActivityType; ");
            db.execSQL("DELETE FROM FarmActivity; ");
            db.execSQL("DELETE FROM FarmSubActivity; ");
            db.execSQL("DELETE FROM PlannedActivity; ");
            db.execSQL("DELETE FROM AllActivity; ");
            db.execSQL("DELETE FROM PlantStatus; ");
            db.execSQL("DELETE FROM Defect; ");
            db.execSQL("DELETE FROM Nursery; ");
            db.execSQL("DELETE FROM NurseryZone; ");
            db.execSQL("DELETE FROM NurseryCoordinates; ");
            db.execSQL("DELETE FROM NurseryAccountDetail; ");
            db.execSQL("DELETE FROM NurseryLandCharacteristic; ");
            db.execSQL("DELETE FROM NurseryCroppingPattern; ");
            db.execSQL("DELETE FROM NurseryLandIssue; ");
            db.execSQL("DELETE FROM NurseryCoordinates; ");
            db.execSQL("DELETE FROM NurseryPlantation; ");
            db.execSQL("DELETE FROM NurseryPlantationSyncTable; ");
            db.execSQL("DELETE FROM NurseryInterCropping; ");
            db.execSQL("DELETE FROM NurseryInterCroppingSyncTable; ");
            db.execSQL("DELETE FROM PlantationWeek; ");
            db.execSQL("DELETE FROM TempJobCardFile; ");
            db.execSQL("DELETE FROM JobCardDetailTemp; ");
            db.execSQL("DELETE FROM JobCard; ");
            db.execSQL("DELETE FROM JobCardDetail; ");
            db.execSQL("DELETE FROM JobCardPending; ");
            result = "success";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Proof details from temporary table by document Id">
    public String deleteProofDetails(String documentId) {
        selectQuery = "DELETE FROM FarmerProofTemp WHERE Id = '" + documentId + "' ";
        db.execSQL(selectQuery);
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete File details from temporary table by type">
    public String DeleteTempFileByType(String type) {
        result = "fail";
        db.execSQL("DELETE FROM TempFile WHERE Type ='" + type + "' ");
        result = "success";
        return result;
    }
    //</editor-fold>


    //<editor-fold desc="Code to delete Job Card File details from temporary table by type">
    public String DeleteJobCardTempFileByType(String activityId, String subActivityId, String type) {
        result = "fail";
        db.execSQL("DELETE FROM TempJobCardFile WHERE Type ='" + type + "' AND ActivityId='" + activityId + "'  AND SubActivityId='" + subActivityId + "'");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete File details from TempFarmerDocument table by type">
    public String DeleteTempFarmerDocumentByType(String type, String farmerUniqueId) {
        result = "fail";
        db.execSQL("DELETE FROM TempFarmerDocument WHERE Type ='" + type + "' AND FarmerUniqueId ='" + farmerUniqueId + "' ");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete File details from TempFarmerDocument table by Unique Id">
    public String DeleteTempFarmerByUniqueId(String farmerUniqueId) {
        result = "fail";
        db.execSQL("DELETE FROM TempFarmerDocument WHERE FarmerUniqueId ='" + farmerUniqueId + "' ");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete File details from temporary table">
    public String DeleteTempFiles() {
        result = "fail";
        db.execSQL("DELETE FROM TempFile");
        db.execSQL("DELETE FROM FarmerTemp");
        db.execSQL("DELETE FROM FarmerOtherDetailsTemp");

        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Proof details from temporary table">
    public String DeleteTempProofs() {
        result = "fail";
        db.execSQL("Delete FROM FarmerProofTemp");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Assigned Blocks from temporary table">
    public String DeleteTempAssignedBlocks() {
        result = "fail";
        db.execSQL("Delete FROM FarmerOperatingBlocksTemp");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Assigned Blocks from temporary table By Id">
    public String DeleteTempAssignedBlocksById(String id) {
        result = "fail";
        db.execSQL("Delete FROM FarmerOperatingBlocksTemp WHERE Id = '" + id + "' ");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Family Members from Temporary Table By Farmer Unique Id and Id">
    public String deleteFamilyMembersTemp(String familyMemberId, String farmerUniqueId) {
        selectQuery = "DELETE FROM FarmerFamilyMemberTemp WHERE Id = '" + familyMemberId + "' AND FarmerUniqueId ='" + farmerUniqueId + "' ";
        db.execSQL(selectQuery);
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Loan Details from Temporary Table By Farmer Unique Id and Id">
    public String deleteFarmerLoanDetailsTemp(String loanId, String farmerUniqueId) {
        selectQuery = "DELETE FROM FarmerLoanDetailsTemp WHERE Id = '" + loanId + "' AND FarmerUniqueId ='" + farmerUniqueId + "' ";
        db.execSQL(selectQuery);
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Family Members from Temporary Table">
    public String deleteFamilyMembersTempData() {
        selectQuery = "DELETE FROM FarmerFamilyMemberTemp ";
        db.execSQL(selectQuery);
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Loan Details from Temporary Table">
    public String deleteLoanDetailsTempData() {
        selectQuery = "DELETE FROM FarmerLoanDetailsTemp ";
        db.execSQL(selectQuery);
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Farmer Asset Details by Farmer Unique Id">
    public String deleteFarmerAssetDetailsByFarmerUniqueId(String farmerUniqueId) {
        selectQuery = "DELETE FROM FarmerAssetDetails WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
        db.execSQL(selectQuery);
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Farm Block Land Characteristic Details by Farm Block Unique Id">
    public String deleteFarmBlockLandCharacteristicByFarmBlockUniqueId(String farmBlockUniqueId) {
        selectQuery = "DELETE FROM FarmBlockLandCharacteristic WHERE FarmBlockUniqueId ='" + farmBlockUniqueId + "' ";
        db.execSQL(selectQuery);
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Farm Block Land Issue Details by Farm Block Unique Id">
    public String deleteFarmBlockLandIssueByFarmBlockUniqueId(String farmBlockUniqueId) {
        selectQuery = "DELETE FROM FarmBlockLandIssue WHERE FarmBlockUniqueId ='" + farmBlockUniqueId + "' ";
        db.execSQL(selectQuery);
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete GPS Data from Temporary Table">
    public String deleteTempGPSData() {
        result = "fail";
        try {
            newValues = new ContentValues();
            db.execSQL("DELETE FROM TempGPS; ");
            result = "success";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete farm block cropping pattern By id">
    public String deleteFarmBlockCroppingPattern(String id) {
        selectQuery = "DELETE FROM FarmBlockCroppingPattern WHERE Id = '" + id + "' ";
        db.execSQL(selectQuery);
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Method to delete details in FarmBlockCoordinates Table">
    public String deleteFarmBlockCoordinates(String farmBlockUniqueId) {
        result = "fail";
        db.execSQL("DELETE FROM FarmBlockCoordinates Where FarmBlockUniqueId = '" + farmBlockUniqueId + "'");
        db.execSQL("DELETE FROM TempGPS");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete inter cropping By id">
    public String deleteInterCropping(String id) {
        selectQuery = "DELETE FROM InterCropping WHERE Id = '" + id + "' ";
        db.execSQL(selectQuery);
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Nursery Inter Cropping By id">
    public String deleteNurseryInterCropping(String id) {
        selectQuery = "DELETE FROM NurseryInterCropping WHERE Id = '" + id + "' ";
        db.execSQL(selectQuery);
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Planned Activities">
    public String deletePlannedActivity() {
        db.execSQL("DELETE FROM PlannedActivity; ");
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete All Activities">
    public String deleteAllActivity() {
        db.execSQL("DELETE FROM AllActivity; ");
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Pending Job Card">
    public String deletePendingJobCard() {
        db.execSQL("DELETE FROM JobCardPending; ");
        return "success";
    }
    //</editor-fold>


    //<editor-fold desc="Code to delete All Plantation Week">
    public String deleteAllPlantationWeek() {
        db.execSQL("DELETE FROM PlantationWeek; ");
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Recommended Activities">
    public String deleteRecommendedActivity() {
        db.execSQL("DELETE FROM RecommendedActivity; ");
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Blank Images">
    public String deleteBlankImages() {
        db.execSQL("DELETE FROM FarmerDocuments WHERE FileName=''; ");
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Method to delete temp record of VisitReport, VisitReportDetail and VisitReportPhoto Table">
    public String DeleteTempVisitReport() {
        try {
            result = "fail";
            db.execSQL("DELETE FROM VisitReport WHERE IsTemp ='1'");
            db.execSQL("DELETE FROM VisitReportDetail WHERE IsTemp ='1'");
            db.execSQL("DELETE FROM VisitReportPhoto WHERE IsTemp ='1'");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete defect By id from VisitReportDetail table and photo from VisitReportPhoto table">
    public String DeleteDefectVisitReportDetail(String id, String visitUniqueId, String defectId) {
        db.execSQL("DELETE FROM VisitReportDetail WHERE Id = '" + id + "';");
        db.execSQL("DELETE FROM VisitReportPhoto WHERE VisitUniqueId = '" + visitUniqueId + "' AND DefectId = '" + defectId + "';");
        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Unplanned Activities from Temporary Table">
    public String DeleteUnPlannedActivitiesFromTempTable(String uniqueId) {
        String actId = "";
        String subActId = "";
        selectQuery = "SELECT FarmActivityId, FarmSubActivityId FROM JobCardDetailTemp WHERE UniqueId = '" + uniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                actId = cursor.getString(0);
                subActId = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        db.execSQL("DELETE FROM JobCardDetailTemp WHERE UniqueId = '" + uniqueId + "';");
        db.execSQL("DELETE FROM TempJobCardFile WHERE ActivityId = '" + uniqueId + "' AND SubActivityId ='" + uniqueId + "' AND Type = 'Additional';");

        return "success";
    }
    //</editor-fold>

    //<editor-fold desc="Code to delete Nursery GPS Data from Temporary Table">
    public String deleteTempNurseryGPSData() {
        result = "fail";
        try {
            newValues = new ContentValues();
            db.execSQL("DELETE FROM TempNurseryGPS; ");
            result = "success";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Method to delete details in NurseryCoordinates Table">
    public String deleteNurseryCoordinates(String nurseryId, String nurseryZoneId) {
        result = "fail";
        db.execSQL("DELETE FROM NurseryCoordinates Where NurseryId = '" + nurseryId + "' AND NurseryZoneId = '" + nurseryZoneId + "'");
        db.execSQL("DELETE FROM TempNurseryGPS");
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="To Delete Data from Plantation Sync Table">
    public String DeleteNurseryPlantationSyncTable() {
        try {
            String query = "DELETE FROM NurseryPlantationSyncTable ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="To Delete Data from Plantation Sync Table">
    public String DeleteNurseryInterCroppingSyncTable() {
        try {
            String query = "DELETE FROM NurseryInterCroppingSyncTable ";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to delete temp record of Recommendation and RecommendationDetail Table">
    public String DeleteTempRecommendation() {
        try {
            result = "fail";
            db.execSQL("DELETE FROM Recommendation WHERE IsTemp ='1'");
            db.execSQL("DELETE FROM RecommendationDetail WHERE IsTemp ='1'");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Method to delete recommendation details">
    public String DeleteRecommendationByUniqueId(String uniqueId) {
        try {
            result = "fail";
            db.execSQL("DELETE FROM RecommendationDetail WHERE UniqueId ='" + uniqueId + "'");
            result = "success";
            return result;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Delete all JobCards">
    public String deleteJobCards() {
        try {

            db.execSQL("DELETE FROM JobCard; ");
            db.execSQL("DELETE FROM JobCardDetail; ");
            result = "success";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Delete all Booking Collection">
    public String deleteBookingCollections() {
        try {
            db.execSQL("DELETE FROM Booking; ");
            result = "success";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="To Delete Data from Pending Job Card">
    public String DeletePendingJobCard() {
        try {
            String query = "DELETE FROM JobCardPending WHERE IsSync = '0'";
            db.execSQL(query);
            result = "success";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //</editor-fold>

    //--------------------------------------------End of Delete Queries---------------------------------------------//


    //--------------------------------------------Start of Select Queries---------------------------------------------//
    //<editor-fold desc="Code to check whether auto synchronize is required">
    public boolean IsSyncRequired() {
        boolean isRequired = true;

        int stateCount;
        selectQuery = "SELECT * FROM State";
        cursor = db.rawQuery(selectQuery, null);
        stateCount = cursor.getCount();
        cursor.close();

        int districtCount;
        selectQuery = "SELECT * FROM District";
        cursor = db.rawQuery(selectQuery, null);
        districtCount = cursor.getCount();
        cursor.close();

        int blockCount;
        selectQuery = "SELECT * FROM Block";
        cursor = db.rawQuery(selectQuery, null);
        blockCount = cursor.getCount();
        cursor.close();

        int panchCount;
        selectQuery = "SELECT * FROM Panchayat";
        cursor = db.rawQuery(selectQuery, null);
        panchCount = cursor.getCount();
        cursor.close();

        int villCount;
        selectQuery = "SELECT * FROM Village";
        cursor = db.rawQuery(selectQuery, null);
        villCount = cursor.getCount();
        cursor.close();

        int cityCount;
        selectQuery = "SELECT * FROM City";
        cursor = db.rawQuery(selectQuery, null);
        cityCount = cursor.getCount();
        cursor.close();

        int pinCount;
        selectQuery = "SELECT * FROM PinCode";
        cursor = db.rawQuery(selectQuery, null);
        pinCount = cursor.getCount();
        cursor.close();

        int relCount;
        selectQuery = "SELECT * FROM RelationShip";
        cursor = db.rawQuery(selectQuery, null);
        relCount = cursor.getCount();
        cursor.close();

        int ftypeCount;
        selectQuery = "SELECT * FROM FarmerType";
        cursor = db.rawQuery(selectQuery, null);
        ftypeCount = cursor.getCount();
        cursor.close();

        int langCount;
        selectQuery = "SELECT * FROM Languages";
        cursor = db.rawQuery(selectQuery, null);
        langCount = cursor.getCount();
        cursor.close();

        int eduCount;
        selectQuery = "SELECT * FROM EducationLevel";
        cursor = db.rawQuery(selectQuery, null);
        eduCount = cursor.getCount();
        cursor.close();

        if (stateCount > 0 && pinCount > 0 && ftypeCount > 0 && langCount > 0 && eduCount > 0)
            isRequired = false;

        return isRequired;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get file details from Temporary Table">
    public ArrayList<HashMap<String, String>> GetTempAttachment(String type) {
        wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT FileName FROM TempFile WHERE Type ='" + type + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            map.put("FileName", cursor.getString(0));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get File Name By Type">
    public String getFileNameForAdditional(String type) {
        String fileName = "";
        selectQuery = "SELECT FileName FROM TempFile WHERE Type ='" + type + "' ";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                fileName = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        return fileName;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get file details from Temporary Table">
    public ArrayList<HashMap<String, String>> GetTempFarmerDocument(String type, String farmerUniqueId) {
        wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT FileName FROM TempFarmerDocument WHERE Type ='" + type + "' AND FarmerUniqueId ='" + farmerUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            map.put("FileName", cursor.getString(0));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Get Masters details">
    public List<CustomType> GetMasterDetails(String masterType, String filter) {
        List<CustomType> labels = new ArrayList<CustomType>();

        switch (masterType) {
            case "state":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT StateId, StateName FROM State ORDER BY StateName COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT StateId, StateNameLocal FROM State ORDER BY StateName COLLATE NOCASE ASC";
                break;
            case "district":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT DistrictId, DistrictName FROM District WHERE StateId=" + filter + " ORDER BY DistrictName COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT DistrictId, DistrictNameLocal FROM District WHERE StateId=" + filter + " ORDER BY DistrictName COLLATE NOCASE ASC";
                break;
            case "alldistrict":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT DistrictId, DistrictName FROM District ORDER BY DistrictName COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT DistrictId, DistrictNameLocal FROM District ORDER BY DistrictName COLLATE NOCASE ASC";
                break;
            case "block":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT BlockId, BlockName FROM Block WHERE DistrictId=" + filter + " ORDER BY BlockName COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT BlockId, BlockNameLocal  FROM Block WHERE DistrictId=" + filter + " ORDER BY BlockName COLLATE NOCASE ASC";
                break;
            case "addressblock":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT bl.BlockId, bl.BlockName FROM Block bl, Panchayat pa WHERE bl.BlockId = pa.BlockId AND bl.DistrictId=" + filter + " ORDER BY bl.BlockName COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT bl.BlockId, bl.BlockNameLocal FROM Block bl, Panchayat pa WHERE bl.BlockId = pa.BlockId AND bl.DistrictId=" + filter + " ORDER BY bl.BlockName COLLATE NOCASE ASC";
                break;
            case "panchayat":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT PanchayatId, PanchayatName FROM Panchayat WHERE BlockId=" + filter + " ORDER BY PanchayatName COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT PanchayatId, PanchayatNameLocal FROM Panchayat WHERE BlockId=" + filter + " ORDER BY PanchayatName COLLATE NOCASE ASC";
                break;
            case "village":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT VillageId, VillageName FROM Village WHERE PanchayatId=" + filter + " ORDER BY VillageName COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT VillageId, VillageNameLocal FROM Village WHERE PanchayatId=" + filter + " ORDER BY VillageName COLLATE NOCASE ASC";
                break;
            case "villagepincode":
                selectQuery = "SELECT DISTINCT PinCodeId, PinCode FROM PinCode WHERE VillageId=" + filter + " ORDER BY PinCode COLLATE NOCASE ASC";
                break;
            case "citypincode":
                selectQuery = "SELECT DISTINCT PinCodeId, PinCode FROM PinCode WHERE CityId=" + filter + " ORDER BY PinCode COLLATE NOCASE ASC";
                break;
            case "city":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT CityId, CityName FROM City WHERE DistrictId=" + filter + " ORDER BY CityName COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT CityId, CityNameLocal FROM City WHERE DistrictId=" + filter + " ORDER BY CityName COLLATE NOCASE ASC";
                break;
            case "farmertype":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT FarmerTypeId, FarmerType FROM FarmerType ORDER BY FarmerType COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT FarmerTypeId, FarmerTypeLocal FROM FarmerType ORDER BY FarmerType COLLATE NOCASE ASC";
                break;
            case "educationlevel":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT EducationLevelId, EducationLevel FROM EducationLevel ORDER BY EducationLevel COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT EducationLevelId, EducationLevelLocal FROM EducationLevel ORDER BY EducationLevel COLLATE NOCASE ASC";
                break;
            case "language":
                selectQuery = "SELECT DISTINCT Id, Name FROM Languages WHERE LOWER(Name) !=LOWER('English') ORDER BY Name COLLATE NOCASE ASC";
                break;
            case "salutation":
                selectQuery = "SELECT  '1', 'Mr.' UNION SELECT  '2', 'Ms.' UNION SELECT  '3', 'Mrs.' UNION SELECT  '4', 'Miss.' UNION SELECT  '5', 'Dr.' UNION SELECT  '6', 'Prof.' UNION SELECT  '7', 'M/s'";
                break;
            case "fathersalutation":
                selectQuery = "SELECT  '1', 'Mr.' UNION SELECT  '2', 'Dr.' UNION SELECT  '3', 'Prof.' UNION SELECT  '4', 'Late'";
                break;
            case "soiltype":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM SoilType ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM SoilType ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "irrigationsystem":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM IrrigationSystem ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM IrrigationSystem ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "nearestriver":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM NearestRiver ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM NearestRiver ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "nearestdam":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM NearestDam ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM NearestDam ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "watersource":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM WaterSource ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM WaterSource ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "electricitysource":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM ElectricitySource ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM ElectricitySource ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "fpoOrganizer":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM Organizer ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM Organizer ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "relationship":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM RelationShip ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM RelationShip ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "loansource":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM LoanSource ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM LoanSource ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "loantype":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM LoanType ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM LoanType ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "landtype":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM LandType ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM LandType ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "existinguse":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM ExistingUse ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM ExistingUse ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "communityuse":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM CommunityUse ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM CommunityUse ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "existinghazard":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM ExistingHazard ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM ExistingHazard ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "legaldispute":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM LegalDispute ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM LegalDispute ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "crop":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM Crop ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM Crop ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "plantationcrop":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM Crop WHERE IsPlantation ='1' ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM Crop WHERE IsPlantation ='1' ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "season":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM Season ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM Season ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "variety":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM Variety WHERE CropId=" + filter + " ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHIndi FROM Variety WHERE CropId=" + filter + " ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "monthAge":
                selectQuery = "SELECT DISTINCT Id, Title FROM Monthage ORDER BY CAST(Title AS NUMBERIC) COLLATE NOCASE ASC";
                break;
            case "plantTypeFarmBlock":
                selectQuery = "SELECT DISTINCT Id, Title FROM PlantType WHERE Title ='Plantation' ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "plantType":
                selectQuery = "SELECT DISTINCT Id, Title FROM PlantType ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "plantingSystem":
                selectQuery = "SELECT DISTINCT Id, Title FROM PlantingSystem ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "farmblockbyfarmer":
                selectQuery = "SELECT DISTINCT Id,FarmBlockCode FROM FarmBlock WHERE FarmerId='" + filter + "' ORDER BY FarmBlockCode COLLATE NOCASE ASC";
            case "defect":
                selectQuery = "SELECT DISTINCT Id, Title FROM Defect ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "plantstatus":
                if (userlang.equalsIgnoreCase("en"))
                    selectQuery = "SELECT DISTINCT Id, Title FROM PlantStatus ORDER BY Title COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT Id, TitleHindi FROM PlantStatus ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "nursery":
                selectQuery = "SELECT DISTINCT Id, Title FROM Nursery ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "nurseryzone":
                selectQuery = "SELECT DISTINCT Id, Title FROM NurseryZone WHERE NurseryId=" + filter + " ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "nurserybytype":
                selectQuery = "SELECT DISTINCT Id, Title FROM Nursery WHERE NurseryType='" + filter + "' ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "paymentmode":
                selectQuery = "SELECT DISTINCT Id, Title FROM PaymentMode ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "shortclosereason":
                selectQuery = "SELECT DISTINCT Id, Title FROM ShortCloseReason ORDER BY Title COLLATE NOCASE ASC";
                break;
        }
        cursor = db.rawQuery(selectQuery, null);

        if (userlang.equalsIgnoreCase("en")) {
            if (masterType.equalsIgnoreCase("state"))
                labels.add(new CustomType(0, "...Select State"));
            else if (masterType.equalsIgnoreCase("district"))
                labels.add(new CustomType(0, "...Select District"));
            else if (masterType.equalsIgnoreCase("alldistrict"))
                labels.add(new CustomType(0, "...Select District"));
            else if (masterType.equalsIgnoreCase("block"))
                labels.add(new CustomType(0, "...Select Block"));
            else if (masterType.equalsIgnoreCase("addressblock"))
                labels.add(new CustomType(0, "...Select Block"));
            else if (masterType.equalsIgnoreCase("panchayat"))
                labels.add(new CustomType(0, "...Select Panchayat"));
            else if (masterType.equalsIgnoreCase("village"))
                labels.add(new CustomType(0, "...Select Village"));
            else if (masterType.equalsIgnoreCase("farmertype"))
                labels.add(new CustomType(0, "...Select Farmer Type"));
            else if (masterType.equalsIgnoreCase("educationlevel"))
                labels.add(new CustomType(0, "...Select Education Level"));
            else if (masterType.equalsIgnoreCase("city"))
                labels.add(new CustomType(0, "...Select City"));
            else if (masterType.equalsIgnoreCase("villagepincode") || masterType.equalsIgnoreCase("citypincode"))
                labels.add(new CustomType(0, "...Select Pincode"));
            else if (masterType.equalsIgnoreCase("soiltype"))
                labels.add(new CustomType(0, "...Select Soil Type"));
            else if (masterType.equalsIgnoreCase("irrigationsystem"))
                labels.add(new CustomType(0, "...Select Irrigation System"));
            else if (masterType.equalsIgnoreCase("nearestriver"))
                labels.add(new CustomType(0, "...Select River"));
            else if (masterType.equalsIgnoreCase("nearestdam"))
                labels.add(new CustomType(0, "...Select Dam"));
            else if (masterType.equalsIgnoreCase("watersource"))
                labels.add(new CustomType(0, "...Select Water Source"));
            else if (masterType.equalsIgnoreCase("electricitysource"))
                labels.add(new CustomType(0, "...Select Electricity Source"));
            else if (masterType.equalsIgnoreCase("fpoOrganizer"))
                labels.add(new CustomType(0, "...Select FPO"));
            else if (masterType.equalsIgnoreCase("relationship"))
                labels.add(new CustomType(0, "...Select Relationship"));
            else if (masterType.equalsIgnoreCase("loansource"))
                labels.add(new CustomType(0, "...Select Loan Source"));
            else if (masterType.equalsIgnoreCase("loantype"))
                labels.add(new CustomType(0, "...Select Loan Type"));
            else if (masterType.equalsIgnoreCase("existinguse"))
                labels.add(new CustomType(0, "...Select Existing Use"));
            else if (masterType.equalsIgnoreCase("communityuse"))
                labels.add(new CustomType(0, "...Select Community Use"));
            else if (masterType.equalsIgnoreCase("existinghazard"))
                labels.add(new CustomType(0, "...Select Existing Hazard"));
            else if (masterType.equalsIgnoreCase("legaldispute"))
                labels.add(new CustomType(0, "...Select Legal Dispute"));
            else if (masterType.equalsIgnoreCase("landtype"))
                labels.add(new CustomType(0, "...Select Ownership"));
            else if (masterType.equalsIgnoreCase("crop"))
                labels.add(new CustomType(0, "...Select Crop"));
            else if (masterType.equalsIgnoreCase("plantationcrop"))
                labels.add(new CustomType(0, "...Select Crop"));
            else if (masterType.equalsIgnoreCase("variety"))
                labels.add(new CustomType(0, "...Select Variety"));
            else if (masterType.equalsIgnoreCase("season"))
                labels.add(new CustomType(0, "...Select Season"));
            else if (masterType.equalsIgnoreCase("monthAge"))
                labels.add(new CustomType(0, "...Select Month Age"));
            else if (masterType.equalsIgnoreCase("plantTypeFarmBlock"))
                labels.add(new CustomType(0, "...Select Plant Type"));
            else if (masterType.equalsIgnoreCase("plantType"))
                labels.add(new CustomType(0, "...Select Plant Type"));
            else if (masterType.equalsIgnoreCase("plantingSystem"))
                labels.add(new CustomType(0, "...Select Planting System"));
            else if (masterType.equalsIgnoreCase("plantstatus"))
                labels.add(new CustomType(0, "...Select Plant Status"));
            else if (masterType.equalsIgnoreCase("defect"))
                labels.add(new CustomType(0, "...Select Defect"));
            else if (masterType.equalsIgnoreCase("nursery") || masterType.equalsIgnoreCase("nurserybytype"))
                labels.add(new CustomType(0, "...Select Nursery"));
            else if (masterType.equalsIgnoreCase("nurseryzone"))
                labels.add(new CustomType(0, "...Select Nursery Zone"));
            else if (masterType.equalsIgnoreCase("paymentmode"))
                labels.add(new CustomType(0, "...Select Payment Mode"));
            else if (masterType.equalsIgnoreCase("shortclosereason"))
                labels.add(new CustomType(0, "...Select ShortClose Reason"));
            else
                labels.add(new CustomType(0, "...Select"));
        } else {
            if (masterType.equalsIgnoreCase("state"))
                labels.add(new CustomType(0, "राज्य का चयन करें "));
            else if (masterType.equalsIgnoreCase("district"))
                labels.add(new CustomType(0, "जिला का चयन करें"));
            else if (masterType.equalsIgnoreCase("alldistrict"))
                labels.add(new CustomType(0, "जिला का चयन करें "));
            else if (masterType.equalsIgnoreCase("block"))
                labels.add(new CustomType(0, "ब्लॉक का चयन करें "));
            else if (masterType.equalsIgnoreCase("addressblock"))
                labels.add(new CustomType(0, "ब्लॉक का चयन करें "));
            else if (masterType.equalsIgnoreCase("panchayat"))
                labels.add(new CustomType(0, "पंचायत का चयन करें "));
            else if (masterType.equalsIgnoreCase("village"))
                labels.add(new CustomType(0, "ग्राम का चयन करें "));
            else if (masterType.equalsIgnoreCase("farmertype"))
                labels.add(new CustomType(0, "किसान प्रकार का चयन करें "));
            else if (masterType.equalsIgnoreCase("educationlevel"))
                labels.add(new CustomType(0, "शिक्षा  स्तर का चयन करें "));
            else if (masterType.equalsIgnoreCase("city"))
                labels.add(new CustomType(0, "शहर का चयन करें "));
            else if (masterType.equalsIgnoreCase("villagepincode") || masterType.equalsIgnoreCase("citypincode"))
                labels.add(new CustomType(0, "पिन कोड का चयन करें "));
            else if (masterType.equalsIgnoreCase("soiltype"))
                labels.add(new CustomType(0, "मिट्टी के प्रकार का चयन करें "));
            else if (masterType.equalsIgnoreCase("irrigationsystem"))
                labels.add(new CustomType(0, "सिंचाई तंत्र का चयन करें "));
            else if (masterType.equalsIgnoreCase("nearestriver"))
                labels.add(new CustomType(0, "नदी का चयन करें "));
            else if (masterType.equalsIgnoreCase("nearestdam"))
                labels.add(new CustomType(0, "बांध का चयन करें "));
            else if (masterType.equalsIgnoreCase("watersource"))
                labels.add(new CustomType(0, "जल स्रोत का चयन करें "));
            else if (masterType.equalsIgnoreCase("electricitysource"))
                labels.add(new CustomType(0, "विद्युत स्रोत का चयन करें "));
            else if (masterType.equalsIgnoreCase("fpoOrganizer"))
                labels.add(new CustomType(0, "एफपीओ का चयन करें "));
            else if (masterType.equalsIgnoreCase("relationship"))
                labels.add(new CustomType(0, "संबंध का चयन करें "));
            else if (masterType.equalsIgnoreCase("loansource"))
                labels.add(new CustomType(0, "ऋण स्रोत का चयन करें "));
            else if (masterType.equalsIgnoreCase("loantype"))
                labels.add(new CustomType(0, "ऋण प्रकार का चयन करें "));
            else if (masterType.equalsIgnoreCase("existinguse"))
                labels.add(new CustomType(0, "मौजूदा उपयोग का चयन करें "));
            else if (masterType.equalsIgnoreCase("communityuse"))
                labels.add(new CustomType(0, "सामुदायिक उपयोग का चयन करें "));
            else if (masterType.equalsIgnoreCase("existinghazard"))
                labels.add(new CustomType(0, "मौजूदा खतरे का चयन करें"));
            else if (masterType.equalsIgnoreCase("legaldispute"))
                labels.add(new CustomType(0, "कानूनी विवाद का चयन करें "));
            else if (masterType.equalsIgnoreCase("landtype"))
                labels.add(new CustomType(0, "स्वामित्व का चयन करें "));
            else if (masterType.equalsIgnoreCase("crop"))
                labels.add(new CustomType(0, "फ़सल का चयन करें "));
            else if (masterType.equalsIgnoreCase("plantationcrop"))
                labels.add(new CustomType(0, "फ़सल का चयन करें "));
            else if (masterType.equalsIgnoreCase("variety"))
                labels.add(new CustomType(0, "क़िस्म का चयन करें "));
            else if (masterType.equalsIgnoreCase("season"))
                labels.add(new CustomType(0, "ऋतु का चयन करें "));
            else if (masterType.equalsIgnoreCase("monthAge"))
                labels.add(new CustomType(0, "मंथ ऐज का चयन करें "));
            else if (masterType.equalsIgnoreCase("plantTypeFarmBlock"))
                labels.add(new CustomType(0, "पौधे प्रकार का चयन करें "));
            else if (masterType.equalsIgnoreCase("plantType"))
                labels.add(new CustomType(0, "पौधे प्रकार का चयन करें "));
            else if (masterType.equalsIgnoreCase("plantingSystem"))
                labels.add(new CustomType(0, "रोपण प्रणाली का चयन करें "));
            else if (masterType.equalsIgnoreCase("plantstatus"))
                labels.add(new CustomType(0, "संयंत्र की स्थिति का चयन करें "));
            else if (masterType.equalsIgnoreCase("defect"))
                labels.add(new CustomType(0, "नुक्स का चयन करें "));
            else if (masterType.equalsIgnoreCase("nursery") || masterType.equalsIgnoreCase("nurserybytype"))
                labels.add(new CustomType(0, "पौधशाला का चयन करें "));
            else if (masterType.equalsIgnoreCase("nurseryzone"))
                labels.add(new CustomType(0, "पौधशाला ज़ोन का चयन करें Nursery Zone"));
            else if (masterType.equalsIgnoreCase("shortclosereason"))
                labels.add(new CustomType(0, "संक्षिप्त बंद कारण"));
            else
                labels.add(new CustomType(0, "चयन करें"));
        }
        while (cursor.moveToNext()) {
            labels.add(new CustomType(Integer.parseInt(cursor.getString(0)), cursor.getString(1)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>


    //<editor-fold desc="Get Masters details in local Language">
    public List<CustomType> GetMasterDetailsLocal(String masterType, String filter) {
        List<CustomType> labels = new ArrayList<CustomType>();

        switch (masterType) {
            case "state":
                selectQuery = "SELECT DISTINCT StateId, StateNameLocal FROM State ORDER BY StateName COLLATE NOCASE ASC";
                break;
            case "district":
                selectQuery = "SELECT DISTINCT DistrictId, DistrictNameLocal FROM District WHERE StateId=" + filter + " ORDER BY DistrictName COLLATE NOCASE ASC";
                break;
            case "alldistrict":
                selectQuery = "SELECT DISTINCT DistrictId, DistrictNameLOcal FROM District ORDER BY DistrictName COLLATE NOCASE ASC";
                break;
            case "block":
                selectQuery = "SELECT DISTINCT BlockId, BlockNameLocal FROM Block WHERE DistrictId=" + filter + " ORDER BY BlockName COLLATE NOCASE ASC";
                break;
            case "addressblock":
                selectQuery = "SELECT DISTINCT bl.BlockId, bl.BlockNameLocal FROM Block bl, Panchayat pa WHERE bl.BlockId = pa.BlockId AND bl.DistrictId=" + filter + " ORDER BY bl.BlockName COLLATE NOCASE ASC";
                break;
            case "panchayat":
                selectQuery = "SELECT DISTINCT PanchayatId, PanchayatNameLocal FROM Panchayat WHERE BlockId=" + filter + " ORDER BY PanchayatName COLLATE NOCASE ASC";
                break;
            case "village":
                selectQuery = "SELECT DISTINCT VillageId, VillageNameLocal FROM Village WHERE PanchayatId=" + filter + " ORDER BY VillageName COLLATE NOCASE ASC";
                break;
            case "villagepincode":
                selectQuery = "SELECT DISTINCT PinCodeId, PinCode FROM PinCode WHERE VillageId=" + filter + " ORDER BY PinCode COLLATE NOCASE ASC";
                break;
            case "citypincode":
                selectQuery = "SELECT DISTINCT PinCodeId, PinCode FROM PinCode WHERE CityId=" + filter + " ORDER BY PinCode COLLATE NOCASE ASC";
                break;
            case "city":
                selectQuery = "SELECT DISTINCT CityId, CityNameLocal FROM City WHERE DistrictId=" + filter + " ORDER BY CityName COLLATE NOCASE ASC";
                break;
            case "farmertype":
                selectQuery = "SELECT DISTINCT FarmerTypeId, FarmerTypeLocal FROM FarmerType ORDER BY FarmerType COLLATE NOCASE ASC";
                break;
            case "educationlevel":
                selectQuery = "SELECT DISTINCT EducationLevelId, EducationLevelLocal FROM EducationLevel ORDER BY EducationLevel COLLATE NOCASE ASC";
                break;
            case "language":
                selectQuery = "SELECT DISTINCT Id, Name FROM Languages WHERE LOWER(Name) !=LOWER('English') ORDER BY Name COLLATE NOCASE ASC";
                break;
            case "salutation":
                selectQuery = "SELECT  '1', 'Mr.' UNION SELECT  '2', 'Ms.' UNION SELECT  '3', 'Mrs.' UNION SELECT  '4', 'Miss.' UNION SELECT  '5', 'Dr.' UNION SELECT  '6', 'Prof.' UNION SELECT  '7', 'M/s'";
                break;
            case "fathersalutation":
                selectQuery = "SELECT  '1', 'Mr.' UNION SELECT  '2', 'Dr.' UNION SELECT  '3', 'Prof.' UNION SELECT  '4', 'Late'";
                break;
            case "soiltype":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM SoilType ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "irrigationsystem":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM IrrigationSystem ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "nearestriver":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM NearestRiver ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "nearestdam":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM NearestDam ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "watersource":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM WaterSource ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "electricitysource":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM ElectricitySource ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "fpoOrganizer":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM Organizer ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "relationship":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM RelationShip ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "loansource":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM LoanSource ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "loantype":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM LoanType ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "landtype":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM LandType ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "existinguse":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM ExistingUse ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "communityuse":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM CommunityUse ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "existinghazard":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM ExistingHazard ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "legaldispute":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM LegalDispute ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "crop":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM Crop ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "plantationcrop":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM Crop WHERE IsPlantation ='1' ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "season":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM Season ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "variety":
                selectQuery = "SELECT DISTINCT Id, TitleLocal FROM Variety WHERE CropId=" + filter + " ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "monthAge":
                selectQuery = "SELECT DISTINCT Id, Title FROM Monthage ORDER BY CAST(Title AS NUMBERIC) COLLATE NOCASE ASC";
                break;
            case "plantTypeFarmBlock":
                selectQuery = "SELECT DISTINCT Id, Title FROM PlantType WHERE Title ='Plantation' ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "plantType":
                selectQuery = "SELECT DISTINCT Id, Title FROM PlantType ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "plantingSystem":
                selectQuery = "SELECT DISTINCT Id, Title FROM PlantingSystem ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "farmblockbyfarmer":
                selectQuery = "SELECT DISTINCT Id,FarmBlockCode FROM FarmBlock WHERE FarmerId='" + filter + "' ORDER BY FarmBlockCode COLLATE NOCASE ASC";
            case "defect":
                selectQuery = "SELECT DISTINCT Id, Title FROM Defect ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "plantstatus":
                selectQuery = "SELECT DISTINCT Id, Title FROM PlantStatus ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "nursery":
                selectQuery = "SELECT DISTINCT Id, Title FROM Nursery ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "nurseryzone":
                selectQuery = "SELECT DISTINCT Id, Title FROM NurseryZone WHERE NurseryId=" + filter + " ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "nurserybytype":
                selectQuery = "SELECT DISTINCT Id, Title FROM Nursery WHERE NurseryType='" + filter + "' ORDER BY Title COLLATE NOCASE ASC";
                break;
            case "shortclosereason":
                selectQuery = "SELECT DISTINCT Id, Title FROM ShortCloseReason ORDER BY Title COLLATE NOCASE ASC";
                break;
        }
        cursor = db.rawQuery(selectQuery, null);

        if (masterType.equalsIgnoreCase("state"))
            labels.add(new CustomType(0, "...Select State"));
        else if (masterType.equalsIgnoreCase("district"))
            labels.add(new CustomType(0, "...Select District"));
        else if (masterType.equalsIgnoreCase("alldistrict"))
            labels.add(new CustomType(0, "...Select District"));
        else if (masterType.equalsIgnoreCase("block"))
            labels.add(new CustomType(0, "...Select Block"));
        else if (masterType.equalsIgnoreCase("addressblock"))
            labels.add(new CustomType(0, "...Select Block"));
        else if (masterType.equalsIgnoreCase("panchayat"))
            labels.add(new CustomType(0, "...Select Panchayat"));
        else if (masterType.equalsIgnoreCase("village"))
            labels.add(new CustomType(0, "...Select Village"));
        else if (masterType.equalsIgnoreCase("farmertype"))
            labels.add(new CustomType(0, "...Select Farmer Type"));
        else if (masterType.equalsIgnoreCase("educationlevel"))
            labels.add(new CustomType(0, "...Select Education Level"));
        else if (masterType.equalsIgnoreCase("city"))
            labels.add(new CustomType(0, "...Select City"));
        else if (masterType.equalsIgnoreCase("villagepincode") || masterType.equalsIgnoreCase("citypincode"))
            labels.add(new CustomType(0, "...Select Pincode"));
        else if (masterType.equalsIgnoreCase("soiltype"))
            labels.add(new CustomType(0, "...Select Soil Type"));
        else if (masterType.equalsIgnoreCase("irrigationsystem"))
            labels.add(new CustomType(0, "...Select Irrigation System"));
        else if (masterType.equalsIgnoreCase("nearestriver"))
            labels.add(new CustomType(0, "...Select River"));
        else if (masterType.equalsIgnoreCase("nearestdam"))
            labels.add(new CustomType(0, "...Select Dam"));
        else if (masterType.equalsIgnoreCase("watersource"))
            labels.add(new CustomType(0, "...Select Water Source"));
        else if (masterType.equalsIgnoreCase("electricitysource"))
            labels.add(new CustomType(0, "...Select Electricity Source"));
        else if (masterType.equalsIgnoreCase("fpoOrganizer"))
            labels.add(new CustomType(0, "...Select FPO"));
        else if (masterType.equalsIgnoreCase("relationship"))
            labels.add(new CustomType(0, "...Select Relationship"));
        else if (masterType.equalsIgnoreCase("loansource"))
            labels.add(new CustomType(0, "...Select Loan Source"));
        else if (masterType.equalsIgnoreCase("loantype"))
            labels.add(new CustomType(0, "...Select Loan Type"));
        else if (masterType.equalsIgnoreCase("existinguse"))
            labels.add(new CustomType(0, "...Select Existing Use"));
        else if (masterType.equalsIgnoreCase("communityuse"))
            labels.add(new CustomType(0, "...Select Community Use"));
        else if (masterType.equalsIgnoreCase("existinghazard"))
            labels.add(new CustomType(0, "...Select Existing Hazard"));
        else if (masterType.equalsIgnoreCase("legaldispute"))
            labels.add(new CustomType(0, "...Select Legal Dispute"));
        else if (masterType.equalsIgnoreCase("landtype"))
            labels.add(new CustomType(0, "...Select Ownership"));
        else if (masterType.equalsIgnoreCase("crop"))
            labels.add(new CustomType(0, "...Select Crop"));
        else if (masterType.equalsIgnoreCase("plantationcrop"))
            labels.add(new CustomType(0, "...Select Crop"));
        else if (masterType.equalsIgnoreCase("variety"))
            labels.add(new CustomType(0, "...Select Variety"));
        else if (masterType.equalsIgnoreCase("season"))
            labels.add(new CustomType(0, "...Select Season"));
        else if (masterType.equalsIgnoreCase("monthAge"))
            labels.add(new CustomType(0, "...Select Month Age"));
        else if (masterType.equalsIgnoreCase("plantTypeFarmBlock"))
            labels.add(new CustomType(0, "...Select Plant Type"));
        else if (masterType.equalsIgnoreCase("plantType"))
            labels.add(new CustomType(0, "...Select Plant Type"));
        else if (masterType.equalsIgnoreCase("plantingSystem"))
            labels.add(new CustomType(0, "...Select Planting System"));
        else if (masterType.equalsIgnoreCase("plantstatus"))
            labels.add(new CustomType(0, "...Select Plant Status"));
        else if (masterType.equalsIgnoreCase("defect"))
            labels.add(new CustomType(0, "...Select Defect"));
        else if (masterType.equalsIgnoreCase("nursery") || masterType.equalsIgnoreCase("nurserybytype"))
            labels.add(new CustomType(0, "...Select Nursery"));
        else if (masterType.equalsIgnoreCase("nurseryzone"))
            labels.add(new CustomType(0, "...Select Nursery Zone"));
        else if (masterType.equalsIgnoreCase("shortclosereason"))
            labels.add(new CustomType(0, "...Select ShortClose Reason"));
        else
            labels.add(new CustomType(0, "...Select"));

        while (cursor.moveToNext()) {
            labels.add(new CustomType(Integer.parseInt(cursor.getString(0)), cursor.getString(1)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Get Other Masters details">
    public List<CustomData> GetOtherMasterDetails(String masterType, String filter) {
        List<CustomData> labels = new ArrayList<CustomData>();

        switch (masterType) {
            case "farmblockbyfarmer":
                selectQuery = "SELECT FarmBlockUniqueId,FarmBlockCode FROM FarmBlock WHERE FarmerId='" + filter + "' AND FarmBlockCode IS NOT NULL ORDER BY FarmBlockCode COLLATE NOCASE ASC";
                break;
            case "farmblockforjobcardbyfarmer":
                selectQuery = "SELECT FarmBlockUniqueId,FarmBlockCode FROM FarmBlock WHERE FarmerId='" + filter + "' AND FarmBlockCode IS NOT NULL AND JobCardAllowed = '1' ORDER BY FarmBlockCode COLLATE NOCASE ASC";
                break;
            case "unplannedactivity":
                selectQuery = "SELECT DISTINCT act.ActivityId||'~'||fa.IsSubActivityAllowed||'~'||ifnull(uom.ShortName,'0'), fa.Title FROM AllActivity act LEFT OUTER JOIN (SELECT ActivityId, SubActivityId FROM  (SELECT  FarmBlockNurseryType,FarmBlockNurseryId,PlantationId,PlantationUniqueId,ActivityId,SubActivityId,WeekNo,UOMId,Quantity,Remarks,FromDate,ToDate,FarmBlockNurseryUniqueId FROM RecommendedActivity UNION SELECT  FarmBlockNurseryType,FarmBlockNurseryId,PlantationId,PlantationUniqueId,ActivityId,SubActivityId,WeekNo,UOMId,Quantity,Remarks,FromDate,ToDate,FarmBlockNurseryUniqueId FROM PlannedActivity)  WHERE (CAST((strftime('%s',FromDate)-strftime('%s',DATE('now'))) AS int)/60/60/24) <= 0 AND (CAST((strftime('%s',ToDate)-strftime('%s',DATE('now'))) AS int)/60/60/24)>= 0 AND PlantationUniqueId= '" + filter + "') pl ON act.ActivityId = pl.ActivityId AND act.SubActivityId = pl.SubActivityId, FarmActivity fa LEFT OUTER JOIN UOM uom ON fa.UomId = uom.UomId WHERE pl.ActivityId IS NULL AND act.ActivityId = fa.FarmActivityId AND act.PlantationUniqueId= '" + filter + "' ORDER BY fa.Title COLLATE NOCASE ASC";
                break;
            case "unplannedsubactivity":
                selectQuery = "SELECT DISTINCT act.SubActivityId||'~'||ifnull(uom.ShortName,'0'), fa.SubActivityName FROM AllActivity act LEFT OUTER JOIN (SELECT ActivityId, SubActivityId FROM (SELECT  FarmBlockNurseryType,FarmBlockNurseryId,PlantationId,PlantationUniqueId,ActivityId,SubActivityId,WeekNo,UOMId,Quantity,Remarks,FromDate,ToDate,FarmBlockNurseryUniqueId FROM RecommendedActivity UNION SELECT  FarmBlockNurseryType,FarmBlockNurseryId,PlantationId,PlantationUniqueId,ActivityId,SubActivityId,WeekNo,UOMId,Quantity,Remarks,FromDate,ToDate,FarmBlockNurseryUniqueId FROM PlannedActivity)  WHERE (CAST((strftime('%s',FromDate)-strftime('%s',DATE('now'))) AS int)/60/60/24) <= 0 AND (CAST((strftime('%s',ToDate)-strftime('%s',DATE('now'))) AS int)/60/60/24)>= 0 AND PlantationUniqueId= '" + filter.split("~")[0] + "') pl ON act.ActivityId = pl.ActivityId AND act.SubActivityId = pl.SubActivityId, FarmSubActivity fa LEFT OUTER JOIN UOM uom ON fa.UomId = uom.UomId WHERE pl.ActivityId IS NULL AND act.SubActivityId = fa.FarmSubActivityId AND act.PlantationUniqueId= '" + filter.split("~")[0] + "' AND act.ActivityId ='" + filter.split("~")[1] + "' ORDER BY fa.SubActivityName COLLATE NOCASE ASC";
                break;
            case "allactivity":
                selectQuery = "SELECT DISTINCT act.ActivityId||'~'||fa.IsSubActivityAllowed||'~'||ifnull(uom.ShortName,'0')||'~'||ifnull(uom.UomId,'0'), fa.Title FROM AllActivity act, FarmActivity fa LEFT OUTER JOIN UOM uom ON fa.UomId = uom.UomId WHERE act.ActivityId = fa.FarmActivityId AND act.PlantationUniqueId= '" + filter + "' ORDER BY fa.Title COLLATE NOCASE ASC";
                break;
            case "allsubactivity":
                selectQuery = "SELECT DISTINCT act.SubActivityId||'~'||ifnull(uom.ShortName,'0')||'~'||ifnull(uom.UomId,'0'), fa.SubActivityName FROM AllActivity act, FarmSubActivity fa LEFT OUTER JOIN UOM uom ON fa.UomId = uom.UomId WHERE act.SubActivityId = fa.FarmSubActivityId AND act.PlantationUniqueId= '" + filter.split("~")[0] + "' AND act.ActivityId ='" + filter.split("~")[1] + "' ORDER BY fa.SubActivityName COLLATE NOCASE ASC";
                break;
            case "week":
                selectQuery = "SELECT '1' AS A, 'Next Week' AS B UNION ALL SELECT '2' AS A, 'Next Week + 1' AS B UNION ALL SELECT '3' AS A, 'Next Week + 2' AS B UNION ALL SELECT '4' AS A, 'Next Week + 3' AS B";
                break;

        }


        cursor = db.rawQuery(selectQuery, null);

        if (masterType.equalsIgnoreCase("farmblockbyfarmer"))
            labels.add(new CustomData("0", "...Select Farm Block"));
        else if (masterType.equalsIgnoreCase("farmblockforjobcardbyfarmer"))
            labels.add(new CustomData("0", "...Select Farm Block"));
        else if (masterType.equalsIgnoreCase("unplannedactivity"))
            labels.add(new CustomData("0~0~0", "...Select Activity"));
        else if (masterType.equalsIgnoreCase("unplannedsubactivity"))
            labels.add(new CustomData("0~0~0", "...Select Sub Activity"));
        else if (masterType.equalsIgnoreCase("allactivity"))
            labels.add(new CustomData("0~0~0~0", "...Select Activity"));
        else if (masterType.equalsIgnoreCase("allsubactivity"))
            labels.add(new CustomData("0~0~0~0", "...Select Sub Activity"));
        else if (masterType.equalsIgnoreCase("week"))
            labels.add(new CustomData("0", "...Select Week"));
        else
            labels.add(new CustomData("0", "...Select"));

        while (cursor.moveToNext()) {
            labels.add(new CustomData(cursor.getString(0), cursor.getString(1)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Get Proof details">
    public List<ProofType> GetProofDetails(String masterType, String filter) {
        List<ProofType> labels = new ArrayList<ProofType>();

        switch (masterType) {
            case "type":
                selectQuery = "SELECT DISTINCT Id, Name FROM ProofType ORDER BY LOWER(Name) ASC";
                break;
            case "proof":
                selectQuery = "SELECT DISTINCT Id, Name FROM POAPOI WHERE ProofTypeId ='" + filter + "' ORDER BY LOWER(Name) ASC";
                break;
        }
        cursor = db.rawQuery(selectQuery, null);
        if (userlang.equalsIgnoreCase("en")) {
            if (masterType.equalsIgnoreCase("type"))
                labels.add(new ProofType("0", "...Select Type"));
            else if (masterType.equalsIgnoreCase("proof"))
                labels.add(new ProofType("0", "...Select Proof"));
        } else {
            if (masterType.equalsIgnoreCase("type"))
                labels.add(new ProofType("0", "प्रकार का चयन करें"));
            else if (masterType.equalsIgnoreCase("proof"))
                labels.add(new ProofType("0", "प्रमाण का चयन करें"));
        }
        while (cursor.moveToNext()) {
            labels.add(new ProofType(cursor.getString(0), cursor.getString(1)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if farm block coordinates is already added">
    public Boolean isExistFarmBlockCoordinates(String farmBlockUniqueId) {
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM FarmBlockCoordinates WHERE FarmBlockUniqueId = '" + farmBlockUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get Farm Block Area">
    public Double getFarmBlockArea(String farmBlockUniqueId) {
        Double area = 0.0;
        selectQuery = "SELECT Acerage FROM FarmBlock WHERE FarmBlockUniqueId = '" + farmBlockUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                area = cursor.getDouble(0);
            } while (cursor.moveToNext());
        }
        return area;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Proof Document by Farmer Unique Id">
    public List<AttachmentDetails> getProofDocument(String farmerUniqueId) {
        List<AttachmentDetails> labels = new ArrayList<AttachmentDetails>();
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT t.Id, pt.Name, p.Name, t.DocumentNo, t.FilePath, t.FileName, t.UniqueId,ifnull(IsSync,'') FROM FarmerProofTemp t,ProofType pt, POAPOI p WHERE t.POAPOIId = p.Id AND pt.Id = p.ProofTypeId AND t.FarmerUniqueId = '" + farmerUniqueId + "' ";
        else
            selectQuery = "SELECT t.Id, pt.NameLocal, p.NameLocal, t.DocumentNo, t.FilePath, t.FileName, t.UniqueId,ifnull(IsSync,'') FROM FarmerProofTemp t,ProofType pt, POAPOI p WHERE t.POAPOIId = p.Id AND pt.Id = p.ProofTypeId AND t.FarmerUniqueId = '" + farmerUniqueId + "' ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new AttachmentDetails(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Proof Document from Main table by Farmer Unique Id">
    public List<AttachmentDetails> getProofDocumentFromMain(String farmerUniqueId, String lang) {
        List<AttachmentDetails> labels = new ArrayList<AttachmentDetails>();

        if (lang.equalsIgnoreCase("en"))
            selectQuery = "SELECT DISTINCT t.Id, pt.Name, p.Name, t.DocumentNo, t.FilePath, t.FileName, t.UniqueId,t.IsSync FROM FarmerProof t,ProofType pt, POAPOI p WHERE t.POAPOIId = p.Id AND pt.Id = p.ProofTypeId AND t.FarmerUniqueId = '" + farmerUniqueId + "' ";
        else
            selectQuery = "SELECT DISTINCT t.Id, pt.NameLocal, p.NameLocal, t.DocumentNo, t.FilePath, t.FileName, t.UniqueId,t.IsSync FROM FarmerProof t,ProofType pt, POAPOI p WHERE t.POAPOIId = p.Id AND pt.Id = p.ProofTypeId AND t.FarmerUniqueId = '" + farmerUniqueId + "' ";


        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new AttachmentDetails(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Operational Blocks from Temp Table by Farmer Unique Id">
    public List<OperationalBlocks> getOperationalDistrictTemp(String farmerUniqueId) {
        List<OperationalBlocks> labels = new ArrayList<OperationalBlocks>();

        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT t.Id, d.DistrictName, b.BlockName FROM FarmerOperatingBlocksTemp t, District d, Block b WHERE t.DistrictId = d.DistrictId AND t.BlockId = b.BlockId AND t.FarmerUniqueId = '" + farmerUniqueId + "' ";
        else
            selectQuery = "SELECT t.Id, d.DistrictNameLocal, b.BlockNameLocal FROM FarmerOperatingBlocksTemp t, District d, Block b WHERE t.DistrictId = d.DistrictId AND t.BlockId = b.BlockId AND t.FarmerUniqueId = '" + farmerUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new OperationalBlocks(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Farmers for Display">
    public List<FarmerData> getFarmerList(String searchText, String isProspective, String lang) {
        List<FarmerData> labels = new ArrayList<FarmerData>();

        if (lang.equalsIgnoreCase("en")) {
            if (isProspective.equalsIgnoreCase("0")) {
                if (searchText.equalsIgnoreCase(""))
                    selectQuery = "SELECT DISTINCT fr.FarmerUniqueId,ifnull(fr.FarmerCode,'') AS FarmerCode, fr.Salutation||' '||fr.FirstName||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| fr.MiddleName END)||' '|| fr.LastName AS FarmerName , fr.FatherSalutation||' '||fr.FatherFirstName||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| fr.FatherMiddleName END)||' '|| fr.FatherLastName AS FatherName, fr.Mobile,(CASE WHEN ad.AddressType ='District Based' THEN ifnull(vi.VillageName,'')||' , '||ifnull(bl.BlockName,'') ELSE ifnull(ci.CityName,'') ||' , '|| ifnull(di.DistrictName,'') END) AS Address, ifnull(fr.IsDuplicate,'') FROM Farmer fr  LEFT OUTER JOIN (SELECT DISTINCT FarmerId FROM FarmBlock) fb ON fr.FarmerUniqueId = fb.FarmerId , Address ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId WHERE fr.FarmerUniqueId = ad.FarmerUniqueId ORDER BY fr.FarmerCode,fr.FirstName COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT fr.FarmerUniqueId,ifnull(fr.FarmerCode,'') AS FarmerCode, fr.Salutation||' '||fr.FirstName||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| fr.MiddleName END)||' '|| fr.LastName AS FarmerName , fr.FatherSalutation||' '||fr.FatherFirstName||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| fr.FatherMiddleName END)||' '|| fr.FatherLastName AS FatherName, fr.Mobile,(CASE WHEN ad.AddressType ='District Based' THEN ifnull(vi.VillageName,'')||' , '||ifnull(bl.BlockName,'') ELSE ifnull(ci.CityName,'') ||' , '|| ifnull(di.DistrictName,'') END) AS Address, ifnull(fr.IsDuplicate,'') FROM Farmer fr  LEFT OUTER JOIN (SELECT DISTINCT FarmerId FROM FarmBlock) fb ON fr.FarmerUniqueId = fb.FarmerId , Address ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId WHERE fr.FarmerUniqueId = ad.FarmerUniqueId AND (fr.FirstName LIKE '" + '%' + searchText + '%' + "' OR fr.MiddleName LIKE '" + '%' + searchText + '%' + "' OR fr.LastName LIKE '" + '%' + searchText + '%' + "' OR FatherFirstName LIKE '" + '%' + searchText + '%' + "' OR fr.FatherMiddleName LIKE '" + '%' + searchText + '%' + "' OR fr.FatherLastName LIKE '" + '%' + searchText + '%' + "' OR fr.Mobile LIKE '" + '%' + searchText + '%' + "') ORDER BY fr.FirstName COLLATE NOCASE ASC";
            } else {
                if (searchText.equalsIgnoreCase(""))
                    selectQuery = "SELECT DISTINCT fr.FarmerUniqueId,ifnull(fr.FarmerCode,'') AS FarmerCode, fr.Salutation||' '||fr.FirstName||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| fr.MiddleName END)||' '|| fr.LastName AS FarmerName , fr.FatherSalutation||' '||fr.FatherFirstName||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| fr.FatherMiddleName END)||' '|| fr.FatherLastName AS FatherName, fr.Mobile,(CASE WHEN ad.AddressType ='District Based' THEN ifnull(vi.VillageName,'')||' , '||ifnull(bl.BlockName,'') ELSE ifnull(ci.CityName,'') ||' , '|| ifnull(di.DistrictName,'') END) AS Address, ifnull(fr.IsDuplicate,'') FROM Farmer fr  LEFT OUTER JOIN (SELECT DISTINCT FarmerId FROM FarmBlock) fb ON fr.FarmerUniqueId = fb.FarmerId , Address ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId WHERE fr.FarmerUniqueId = ad.FarmerUniqueId AND fb.FarmerId IS NULL ORDER BY fr.FarmerCode,fr.FirstName COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT fr.FarmerUniqueId,ifnull(fr.FarmerCode,'') AS FarmerCode, fr.Salutation||' '||fr.FirstName||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| fr.MiddleName END)||' '|| fr.LastName AS FarmerName , fr.FatherSalutation||' '||fr.FatherFirstName||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| fr.FatherMiddleName END)||' '|| fr.FatherLastName AS FatherName, fr.Mobile,(CASE WHEN ad.AddressType ='District Based' THEN ifnull(vi.VillageName,'')||' , '||ifnull(bl.BlockName,'') ELSE ifnull(ci.CityName,'') ||' , '|| ifnull(di.DistrictName,'') END) AS Address, ifnull(fr.IsDuplicate,'') FROM Farmer fr  LEFT OUTER JOIN (SELECT DISTINCT FarmerId FROM FarmBlock) fb ON fr.FarmerUniqueId = fb.FarmerId , Address ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId WHERE fr.FarmerUniqueId = ad.FarmerUniqueId AND fb.FarmerId IS NULL AND (fr.FirstName LIKE '" + '%' + searchText + '%' + "' OR fr.MiddleName LIKE '" + '%' + searchText + '%' + "' OR fr.LastName LIKE '" + '%' + searchText + '%' + "' OR FatherFirstName LIKE '" + '%' + searchText + '%' + "' OR fr.FatherMiddleName LIKE '" + '%' + searchText + '%' + "' OR fr.FatherLastName LIKE '" + '%' + searchText + '%' + "' OR fr.Mobile LIKE '" + '%' + searchText + '%' + "') ORDER BY fr.FirstName COLLATE NOCASE ASC";
            }
        } else {
            if (isProspective.equalsIgnoreCase("0")) {
                if (searchText.equalsIgnoreCase(""))
                    selectQuery = "SELECT DISTINCT fr.FarmerUniqueId,ifnull(fr.FarmerCode,'') AS FarmerCode, (CASE WHEN fr.SalutationLocal='' THEN fr.Salutation ELSE fr.SalutationLocal END) ||' '||(CASE WHEN fr.FirstNameLocal='' THEN fr.FirstName ELSE fr.FirstNameLocal END)||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| (CASE WHEN fr.MiddleNameLocal='' THEN fr.MiddleName ELSE fr.MiddleNameLocal END) END)||' '|| (CASE WHEN fr.LastNameLocal='' THEN fr.LastName ELSE fr.LastNameLocal END) AS FarmerName , (CASE WHEN fr.FatherSalutationLocal='' THEN fr.FatherSalutation ELSE fr.FatherSalutationLocal END)||' '||(CASE WHEN fr.FatherFirstNameLocal='' THEN fr.FatherFirstName ELSE fr.FatherFirstNameLocal END)||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| (CASE WHEN fr.FatherMiddleNameLocal='' THEN fr.FatherMiddleName ELSE fr.FatherMiddleNameLocal END) END)||' '|| (CASE WHEN fr.FatherLastNameLocal='' THEN fr.FatherLastName ELSE fr.FatherLastNameLocal END) AS FatherName, fr.Mobile,(CASE WHEN ad.AddressType ='District Based' THEN ifnull(vi.VillageName,'')||' , '||ifnull(bl.BlockName,'') ELSE ifnull(ci.CityName,'') ||' , '|| ifnull(di.DistrictName,'') END) AS Address, ifnull(fr.IsDuplicate,'') FROM Farmer fr  LEFT OUTER JOIN (SELECT DISTINCT FarmerId FROM FarmBlock) fb ON fr.FarmerUniqueId = fb.FarmerId , Address ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId WHERE fr.FarmerUniqueId = ad.FarmerUniqueId ORDER BY fr.FarmerCode,fr.FirstName COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT fr.FarmerUniqueId,ifnull(fr.FarmerCode,'') AS FarmerCode, (CASE WHEN fr.SalutationLocal='' THEN fr.Salutation ELSE fr.SalutationLocal END) ||' '||(CASE WHEN fr.FirstNameLocal='' THEN fr.FirstName ELSE fr.FirstNameLocal END)||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| (CASE WHEN fr.MiddleNameLocal='' THEN fr.MiddleName ELSE fr.MiddleNameLocal END) END)||' '|| (CASE WHEN fr.LastNameLocal='' THEN fr.LastName ELSE fr.LastNameLocal END) AS FarmerName , (CASE WHEN fr.FatherSalutationLocal='' THEN fr.FatherSalutation ELSE fr.FatherSalutationLocal END)||' '||(CASE WHEN fr.FatherFirstNameLocal='' THEN fr.FatherFirstName ELSE fr.FatherFirstNameLocal END)||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| (CASE WHEN fr.FatherMiddleNameLocal='' THEN fr.FatherMiddleName ELSE fr.FatherMiddleNameLocal END) END)||' '|| (CASE WHEN fr.FatherLastNameLocal='' THEN fr.FatherLastName ELSE fr.FatherLastNameLocal END) AS FatherName, fr.Mobile,(CASE WHEN ad.AddressType ='District Based' THEN ifnull(vi.VillageName,'')||' , '||ifnull(bl.BlockName,'') ELSE ifnull(ci.CityName,'') ||' , '|| ifnull(di.DistrictName,'') END) AS Address, ifnull(fr.IsDuplicate,'') FROM Farmer fr  LEFT OUTER JOIN (SELECT DISTINCT FarmerId FROM FarmBlock) fb ON fr.FarmerUniqueId = fb.FarmerId , Address ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId WHERE fr.FarmerUniqueId = ad.FarmerUniqueId AND (fr.FirstName LIKE '" + '%' + searchText + '%' + "' OR fr.MiddleName LIKE '" + '%' + searchText + '%' + "' OR fr.LastName LIKE '" + '%' + searchText + '%' + "' OR FatherFirstName LIKE '" + '%' + searchText + '%' + "' OR fr.FatherMiddleName LIKE '" + '%' + searchText + '%' + "' OR fr.FatherLastName LIKE '" + '%' + searchText + '%' + "' OR fr.Mobile LIKE '" + '%' + searchText + '%' + "') ORDER BY fr.FirstName COLLATE NOCASE ASC";
            } else {
                if (searchText.equalsIgnoreCase(""))
                    selectQuery = "SELECT DISTINCT fr.FarmerUniqueId,ifnull(fr.FarmerCode,'') AS FarmerCode, (CASE WHEN fr.SalutationLocal='' THEN fr.Salutation ELSE fr.SalutationLocal END) ||' '||(CASE WHEN fr.FirstNameLocal='' THEN fr.FirstName ELSE fr.FirstNameLocal END)||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| (CASE WHEN fr.MiddleNameLocal='' THEN fr.MiddleName ELSE fr.MiddleNameLocal END) END)||' '|| (CASE WHEN fr.LastNameLocal='' THEN fr.LastName ELSE fr.LastNameLocal END) AS FarmerName , (CASE WHEN fr.FatherSalutationLocal='' THEN fr.FatherSalutation ELSE fr.FatherSalutationLocal END)||' '||(CASE WHEN fr.FatherFirstNameLocal='' THEN fr.FatherFirstName ELSE fr.FatherFirstNameLocal END)||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| (CASE WHEN fr.FatherMiddleNameLocal='' THEN fr.FatherMiddleName ELSE fr.FatherMiddleNameLocal END) END)||' '|| (CASE WHEN fr.FatherLastNameLocal='' THEN fr.FatherLastName ELSE fr.FatherLastNameLocal END)AS FatherName, fr.Mobile,(CASE WHEN ad.AddressType ='District Based' THEN ifnull(vi.VillageName,'')||' , '||ifnull(bl.BlockName,'') ELSE ifnull(ci.CityName,'') ||' , '|| ifnull(di.DistrictName,'') END) AS Address, ifnull(fr.IsDuplicate,'') FROM Farmer fr  LEFT OUTER JOIN (SELECT DISTINCT FarmerId FROM FarmBlock) fb ON fr.FarmerUniqueId = fb.FarmerId , Address ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId WHERE fr.FarmerUniqueId = ad.FarmerUniqueId AND fb.FarmerId IS NULL ORDER BY fr.FarmerCode,fr.FirstName COLLATE NOCASE ASC";
                else
                    selectQuery = "SELECT DISTINCT fr.FarmerUniqueId,ifnull(fr.FarmerCode,'') AS FarmerCode, (CASE WHEN fr.SalutationLocal='' THEN fr.Salutation ELSE fr.SalutationLocal END) ||' '||(CASE WHEN fr.FirstNameLocal='' THEN fr.FirstName ELSE fr.FirstNameLocal END)||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| (CASE WHEN fr.MiddleNameLocal='' THEN fr.MiddleName ELSE fr.MiddleNameLocal END) END)||' '|| (CASE WHEN fr.LastNameLocal='' THEN fr.LastName ELSE fr.LastNameLocal END) AS FarmerName , (CASE WHEN fr.FatherSalutationLocal='' THEN fr.FatherSalutation ELSE fr.FatherSalutationLocal END)||' '||(CASE WHEN fr.FatherFirstNameLocal='' THEN fr.FatherFirstName ELSE fr.FatherFirstNameLocal END)||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| (CASE WHEN fr.FatherMiddleNameLocal='' THEN fr.FatherMiddleName ELSE fr.FatherMiddleNameLocal END) END)||' '|| (CASE WHEN fr.FatherLastNameLocal='' THEN fr.FatherLastName ELSE fr.FatherLastNameLocal END) AS FatherName, fr.Mobile,(CASE WHEN ad.AddressType ='District Based' THEN ifnull(vi.VillageName,'')||' , '||ifnull(bl.BlockName,'') ELSE ifnull(ci.CityName,'') ||' , '|| ifnull(di.DistrictName,'') END) AS Address, ifnull(fr.IsDuplicate,'') FROM Farmer fr  LEFT OUTER JOIN (SELECT DISTINCT FarmerId FROM FarmBlock) fb ON fr.FarmerUniqueId = fb.FarmerId , Address ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId WHERE fr.FarmerUniqueId = ad.FarmerUniqueId AND fb.FarmerId IS NULL AND (fr.FirstName LIKE '" + '%' + searchText + '%' + "' OR fr.MiddleName LIKE '" + '%' + searchText + '%' + "' OR fr.LastName LIKE '" + '%' + searchText + '%' + "' OR FatherFirstName LIKE '" + '%' + searchText + '%' + "' OR fr.FatherMiddleName LIKE '" + '%' + searchText + '%' + "' OR fr.FatherLastName LIKE '" + '%' + searchText + '%' + "' OR fr.Mobile LIKE '" + '%' + searchText + '%' + "') ORDER BY fr.FirstName COLLATE NOCASE ASC";
            }
        }

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new FarmerData(cursor.getString(0), cursor.getString(2), cursor.getString(3), cursor.getString(1), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Farmers by Search Text">
    public List<FarmerData> getFarmerListBySerachText(String searchText) {
        List<FarmerData> labels = new ArrayList<FarmerData>();

        selectQuery = "SELECT DISTINCT fr.FarmerUniqueId,ifnull(fr.FarmerCode,'') AS FarmerCode, fr.Salutation||' '||fr.FirstName||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| fr.MiddleName END)||' '|| fr.LastName AS FarmerName , fr.FatherSalutation||' '||fr.FatherFirstName||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| fr.FatherMiddleName END)||' '|| fr.FatherLastName AS FatherName, fr.Mobile,(CASE WHEN ad.AddressType ='District Based' THEN ifnull(vi.VillageName,'')||' , '||ifnull(bl.BlockName,'') ELSE ifnull(ci.CityName,'') ||' , '|| ifnull(di.DistrictName,'') END) AS Address, ifnull(fr.IsDuplicate,'') FROM Farmer fr, Address ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId WHERE fr.FarmerUniqueId = ad.FarmerUniqueId AND fr.FarmerCode IS NOT NULL AND (fr.FirstName LIKE '" + '%' + searchText + '%' + "' OR fr.MiddleName LIKE '" + '%' + searchText + '%' + "' OR fr.LastName LIKE '" + '%' + searchText + '%' + "' OR FatherFirstName LIKE '" + '%' + searchText + '%' + "' OR fr.FatherMiddleName LIKE '" + '%' + searchText + '%' + "' OR fr.FatherLastName LIKE '" + '%' + searchText + '%' + "' OR fr.Mobile LIKE '" + '%' + searchText + '%' + "') ORDER BY fr.FirstName COLLATE NOCASE ASC";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new FarmerData(cursor.getString(0), cursor.getString(2), cursor.getString(3), cursor.getString(1), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Plantation By FarmBlock Unique Id">
    public List<CustomData> getPlantationListByFarmBlockId(String farmBlockUniqueId) {
        List<CustomData> labels = new ArrayList<CustomData>();
        selectQuery = "SELECT DISTINCT pl.PlantationUniqueId, (var.Title||' - '|| pl.PlantationDate) AS Name FROM FarmerPlantation pl, Crop cr, Variety var WHERE pl.CropId = cr.Id AND pl.CropVarietyId = var.Id AND pl.FarmBlockUniqueId ='" + farmBlockUniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new CustomData(cursor.getString(0), cursor.getString(1)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>


    //<editor-fold desc="Code to get list of Plantation For Routine Visit By FarmBlock Unique Id">
    public List<CustomData> getRoutinePlantationListByFarmBlockId(String farmBlockUniqueId) {
        List<CustomData> labels = new ArrayList<CustomData>();
        selectQuery = "SELECT DISTINCT pl.PlantationUniqueId, (var.Title||' - '|| REPLACE(pl.PlantationDate,'/','-')) AS Name FROM FarmerPlantation pl, Crop cr, Variety var WHERE pl.CropId = cr.Id AND pl.CropVarietyId = var.Id AND pl.IsSync = '1' AND pl.FarmBlockUniqueId ='" + farmBlockUniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new CustomData(cursor.getString(0), cursor.getString(1)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of nursery plantation  for routine visit by zone id">
    public List<CustomData> getNurseryPlantationListByZoneId(String zoneId) {
        List<CustomData> labels = new ArrayList<CustomData>();
        selectQuery = "SELECT DISTINCT pl.PlantationUniqueId, (var.Title||' - '|| REPLACE(pl.PlantationDate,'/','-')) AS Name FROM NurseryPlantation pl, Crop cr, Variety var, NurseryZone zn WHERE pl.CropId = cr.Id AND pl.CropVarietyId = var.Id AND zn.Id = pl.ZoneId AND pl.NurseryId = zn.NurseryId AND pl.IsSync = '1' AND pl.ZoneId ='" + zoneId + "'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new CustomData(cursor.getString(0), cursor.getString(1)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Method to get POA count for Farmer">
    public int getPOACountByFarmerId(String farmerUniqueId) {
        int id = 0;
        selectQuery = "SELECT COUNT(t.Id) FROM FarmerProofTemp t, POAPOI p WHERE t.POAPOIId = p.Id AND p.ProofTypeId='1' AND  t.FarmerUniqueId = '" + farmerUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        return id;
    }
    //</editor-fold>

    //<editor-fold desc="Method to get POI count for Farmer">
    public int getPOICountByFarmerId(String farmerUniqueId) {
        int id = 0;
        selectQuery = "SELECT COUNT(t.Id) FROM FarmerProofTemp t, POAPOI p WHERE t.POAPOIId = p.Id AND p.ProofTypeId='2' AND  t.FarmerUniqueId = '" + farmerUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        return id;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if block is already added">
    public Boolean isBlockAlreadyAdded(String farmerUniqueId, String districtId, String blockId) {
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM FarmerOperatingBlocksTemp WHERE FarmerUniqueId = '" + farmerUniqueId + "' AND DistrictId ='" + districtId + "' AND BlockId ='" + blockId + "'  ";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="Code to Validate Nominee Percentage">
    public Double totalNomineePercentage(String farmerUniqueId, Double percentage) {
        Double totalPerc = 0.0;
        selectQuery = "SELECT ifnull(SUM(NomineePercentage),0) FROM FarmerFamilyMemberTemp WHERE FarmerUniqueId ='" + farmerUniqueId + "' AND IsNominee = 1  ";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                totalPerc = cursor.getDouble(0);
            } while (cursor.moveToNext());
        }
        totalPerc = totalPerc + percentage;
        cursor.close();
        return totalPerc;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get farmer details by unique id">
    public ArrayList<String> getFarmerDetailsByUniqueId(String farmerUniqueId) {
        ArrayList<String> farmerdetails = new ArrayList<String>();
        String newFileName = "";
        selectQuery = "SELECT ifnull(fr.FarmerCode,'') AS FarmerCode,fr.EducationLevelId,fr.FarmerTypeId,fr.Salutation,fr.FirstName,fr.MiddleName,fr.LastName,fr.FatherSalutation,fr.FatherFirstName,fr.FatherMiddleName,fr.FatherLastName,fr.EmailId,fr.Mobile,fr.Mobile1,fr.Mobile2,fr.BirthDate,fr.Gender,fr.BankAccountNo,fr.IFSCCode,fr.TotalAcreage,fr.FSSAINumber,fr.RegistrationDate,fr.ExpiryDate,ad.Street1,ad.Street2,ad.StateId,ad.DistrictId,ad.BlockId,ad.PanchayatId,ad.VillageId,ad.CityId,ad.PinCodeId,ad.AddressType, fr.LanguageId,ifnull(ot.SoilTypeId,0),ifnull(ot.IrrigationSystemId,0),ifnull(ot.RiverId,0),ifnull(ot.DamId,0),ifnull(ot.WaterSourceId,0),ifnull(ot.ElectricitySourceId,0), ifnull(ph.FileName,''), ifnull(pb.FileName,''), ifnull(fs.FileName,'')  FROM Farmer fr LEFT OUTER JOIN FarmerOtherDetails ot ON fr.FarmerUniqueId = ot.FarmerUniqueId LEFT OUTER JOIN FarmerDocuments ph ON fr.FarmerUniqueId = ph.FarmerUniqueId AND ph.Type ='Farmer'  LEFT OUTER JOIN FarmerDocuments pb ON fr.FarmerUniqueId = pb.FarmerUniqueId AND pb.Type ='PassBook'  LEFT OUTER JOIN FarmerDocuments fs ON fr.FarmerUniqueId = fs.FarmerUniqueId AND fs.Type ='FSSAI', Address ad WHERE fr.FarmerUniqueId = ad.FarmerUniqueId AND fr.FarmerUniqueId = '" + farmerUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            farmerdetails.add(cursor.getString(0));
            farmerdetails.add(cursor.getString(1));
            farmerdetails.add(cursor.getString(2));
            farmerdetails.add(cursor.getString(3));
            farmerdetails.add(cursor.getString(4));
            farmerdetails.add(cursor.getString(5));
            farmerdetails.add(cursor.getString(6));
            farmerdetails.add(cursor.getString(7));
            farmerdetails.add(cursor.getString(8));
            farmerdetails.add(cursor.getString(9));
            farmerdetails.add(cursor.getString(10));
            farmerdetails.add(cursor.getString(11));
            farmerdetails.add(cursor.getString(12));
            farmerdetails.add(cursor.getString(13));
            farmerdetails.add(cursor.getString(14));
            farmerdetails.add(cursor.getString(15));
            farmerdetails.add(cursor.getString(16));
            farmerdetails.add(cursor.getString(17));
            farmerdetails.add(cursor.getString(18));
            farmerdetails.add(cursor.getString(19));
            farmerdetails.add(cursor.getString(20));
            farmerdetails.add(cursor.getString(21));
            farmerdetails.add(cursor.getString(22));
            farmerdetails.add(cursor.getString(23));
            farmerdetails.add(cursor.getString(24));
            farmerdetails.add(cursor.getString(25));
            farmerdetails.add(cursor.getString(26));
            farmerdetails.add(cursor.getString(27));
            farmerdetails.add(cursor.getString(28));
            farmerdetails.add(cursor.getString(29));
            farmerdetails.add(cursor.getString(30));
            farmerdetails.add(cursor.getString(31));
            farmerdetails.add(cursor.getString(32));
            farmerdetails.add(cursor.getString(33));
            farmerdetails.add(cursor.getString(34));
            farmerdetails.add(cursor.getString(35));
            farmerdetails.add(cursor.getString(36));
            farmerdetails.add(cursor.getString(37));
            farmerdetails.add(cursor.getString(38));
            farmerdetails.add(cursor.getString(39));
            farmerdetails.add(cursor.getString(40));
            farmerdetails.add(cursor.getString(41));
            farmerdetails.add(cursor.getString(42));
        }
        cursor.close();

        db.execSQL("INSERT INTO TempFile(Type, FileName,IsSync) SELECT Type, FileName, IsSync FROM FarmerDocuments WHERE FarmerUniqueId = '" + farmerUniqueId + "'");

        return farmerdetails;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get farmer details by unique id from temp table">
    public ArrayList<String> getTempFarmerDetailsByUniqueId(String farmerUniqueId) {
        ArrayList<String> farmerdetails = new ArrayList<String>();
        String newFileName = "";
        selectQuery = "SELECT 0 AS FarmerCode, fr.EducationLevelId,fr.FarmerTypeId,fr.Salutation,fr.FirstName,fr.MiddleName,fr.LastName,fr.FatherSalutation,fr.FatherFirstName,fr.FatherMiddleName,fr.FatherLastName,fr.EmailId,fr.Mobile,fr.Mobile1,fr.Mobile2,fr.BirthDate,fr.Gender,fr.BankAccountNo,fr.IFSCCode,fr.TotalAcreage,fr.FSSAINumber,fr.RegistrationDate,fr.ExpiryDate,ad.Street1,ad.Street2,ad.StateId,ad.DistrictId,ad.BlockId,ad.PanchayatId,ad.VillageId,ad.CityId,ad.PinCodeId,ad.AddressType, fr.LanguageId,ifnull(ot.SoilTypeId,0),ifnull(ot.IrrigationSystemId,0),ifnull(ot.RiverId,0),ifnull(ot.DamId,0),ifnull(ot.WaterSourceId,0),ifnull(ot.ElectricitySourceId,0), ifnull(ph.FileName,''), ifnull(pb.FileName,''), ifnull(fs.FileName,'')  FROM FarmerTemp fr LEFT OUTER JOIN FarmerOtherDetailsTemp ot ON fr.FarmerUniqueId = ot.FarmerUniqueId LEFT OUTER JOIN TempFile ph ON  ph.Type ='Farmer'  LEFT OUTER JOIN TempFile pb ON pb.Type ='PassBook'  LEFT OUTER JOIN TempFile fs ON  fs.Type ='FSSAI', AddressTemp ad WHERE fr.FarmerUniqueId = ad.FarmerUniqueId AND fr.FarmerUniqueId = '" + farmerUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            farmerdetails.add(cursor.getString(0));
            farmerdetails.add(cursor.getString(1));
            farmerdetails.add(cursor.getString(2));
            farmerdetails.add(cursor.getString(3));
            farmerdetails.add(cursor.getString(4));
            farmerdetails.add(cursor.getString(5));
            farmerdetails.add(cursor.getString(6));
            farmerdetails.add(cursor.getString(7));
            farmerdetails.add(cursor.getString(8));
            farmerdetails.add(cursor.getString(9));
            farmerdetails.add(cursor.getString(10));
            farmerdetails.add(cursor.getString(11));
            farmerdetails.add(cursor.getString(12));
            farmerdetails.add(cursor.getString(13));
            farmerdetails.add(cursor.getString(14));
            farmerdetails.add(cursor.getString(15));
            farmerdetails.add(cursor.getString(16));
            farmerdetails.add(cursor.getString(17));
            farmerdetails.add(cursor.getString(18));
            farmerdetails.add(cursor.getString(19));
            farmerdetails.add(cursor.getString(20));
            farmerdetails.add(cursor.getString(21));
            farmerdetails.add(cursor.getString(22));
            farmerdetails.add(cursor.getString(23));
            farmerdetails.add(cursor.getString(24));
            farmerdetails.add(cursor.getString(25));
            farmerdetails.add(cursor.getString(26));
            farmerdetails.add(cursor.getString(27));
            farmerdetails.add(cursor.getString(28));
            farmerdetails.add(cursor.getString(29));
            farmerdetails.add(cursor.getString(30));
            farmerdetails.add(cursor.getString(31));
            farmerdetails.add(cursor.getString(32));
            farmerdetails.add(cursor.getString(33));
            farmerdetails.add(cursor.getString(34));
            farmerdetails.add(cursor.getString(35));
            farmerdetails.add(cursor.getString(36));
            farmerdetails.add(cursor.getString(37));
            farmerdetails.add(cursor.getString(38));
            farmerdetails.add(cursor.getString(39));
            farmerdetails.add(cursor.getString(40));
            farmerdetails.add(cursor.getString(41));
            farmerdetails.add(cursor.getString(42));
        }
        cursor.close();

        return farmerdetails;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get farmer details by unique id from Main table">
    public ArrayList<String> getFarmerDetailsForViewByUniqueId(String farmerUniqueId, String lang) {
        ArrayList<String> farmerdetails = new ArrayList<String>();
        String newFileName = "";
        if (lang.equalsIgnoreCase("en"))
            selectQuery = "SELECT DISTINCT ifnull(fr.FarmerCode,'') AS FarmerCode,el.EducationLevel,ft.FarmerType,fr.Salutation||' '||fr.FirstName||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| fr.MiddleName END)||' '|| fr.LastName AS FarmerName,fr.FatherSalutation||' '||fr.FatherFirstName||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| fr.FatherMiddleName END)||' '|| fr.FatherLastName AS FatherName,fr.EmailId,fr.Mobile,fr.Mobile1,fr.Mobile2,fr.BirthDate,fr.Gender,fr.BankAccountNo,fr.IFSCCode,fr.TotalAcreage,fr.FSSAINumber,fr.RegistrationDate,fr.ExpiryDate,ad.Street1,ad.Street2,ifnull(st.StateName,''),ifnull(dt.DistrictName,''),ifnull(bk.BlockName,''),ifnull(pc.PanchayatName,''),ifnull(vg.VillageName,''),ifnull(ct.CityName,''),pd.PinCode,ad.AddressType, lg.Name,ifnull(tp.Title,0),ifnull(irs.Title,0),ifnull(nr.Title,0),ifnull(nd.Title,0),ifnull(ws.Title,0),ifnull(es.Title,0), ifnull(ph.FileName,''), ifnull(pb.FileName,''), ifnull(fs.FileName,'')  FROM Farmer fr LEFT OUTER JOIN FarmerOtherDetails ot ON fr.FarmerUniqueId = ot.FarmerUniqueId LEFT OUTER JOIN SoilType tp ON ot.SoilTypeId = tp.Id LEFT OUTER JOIN NearestRiver nr ON ot.RiverId = nr.Id LEFT OUTER JOIN NearestDam nd ON ot.DamId = nd.Id LEFT OUTER JOIN IrrigationSystem irs ON ot.IrrigationSystemId = irs.Id LEFT OUTER JOIN WaterSource ws ON ot.WaterSourceId = ws.Id LEFT OUTER JOIN ElectricitySource es ON ot.ElectricitySourceId = es.Id LEFT OUTER JOIN (SELECT DISTINCT FarmerUniqueId,Type,FileName FROM FarmerDocuments) ph ON fr.FarmerUniqueId = ph.FarmerUniqueId AND ph.Type ='Farmer'  LEFT OUTER JOIN (SELECT DISTINCT FarmerUniqueId,Type,FileName FROM FarmerDocuments) pb ON fr.FarmerUniqueId = pb.FarmerUniqueId AND pb.Type ='PassBook'  LEFT OUTER JOIN (SELECT DISTINCT FarmerUniqueId,Type,FileName FROM FarmerDocuments) fs ON fr.FarmerUniqueId = fs.FarmerUniqueId AND fs.Type ='FSSAI', Address ad LEFT OUTER JOIN State st ON ad.StateId = st.StateId LEFT OUTER JOIN District dt ON ad.DistrictId = dt.DistrictId LEFT OUTER JOIN Block bk ON ad.BlockId = bk.BlockId LEFT OUTER JOIN Panchayat pc ON ad.PanchayatId = pc.PanchayatId LEFT OUTER JOIN Village vg ON ad.VillageId = vg.VillageId LEFT OUTER JOIN City ct ON ad.CityId = ct.CityId LEFT OUTER JOIN PinCode pd ON ad.PinCodeId = pd.PinCodeId, EducationLevel el, FarmerType ft, Languages lg  WHERE fr.FarmerUniqueId = ad.FarmerUniqueId AND el.EducationLevelId = fr.EducationLevelId AND ft.FarmerTypeId = fr.FarmerTypeId AND fr.LanguageId = lg.Id  AND fr.FarmerUniqueId ='" + farmerUniqueId + "' ";
        else
            selectQuery = "SELECT DISTINCT ifnull(fr.FarmerCode,'') AS FarmerCode,el.EducationLevelLocal,ft.FarmerTypeLocal,(CASE WHEN fr.SalutationLocal='' THEN fr.Salutation ELSE fr.SalutationLocal END) ||' '||(CASE WHEN fr.FirstNameLocal='' THEN fr.FirstName ELSE fr.FirstNameLocal END)||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| (CASE WHEN fr.MiddleNameLocal='' THEN fr.MiddleName ELSE fr.MiddleNameLocal END) END)||' '|| (CASE WHEN fr.LastNameLocal='' THEN fr.LastName ELSE fr.LastNameLocal END) AS FarmerName , (CASE WHEN fr.FatherSalutationLocal='' THEN fr.FatherSalutation ELSE fr.FatherSalutationLocal END)||' '||(CASE WHEN fr.FatherFirstNameLocal='' THEN fr.FatherFirstName ELSE fr.FatherFirstNameLocal END)||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| (CASE WHEN fr.FatherMiddleNameLocal='' THEN fr.FatherMiddleName ELSE fr.FatherMiddleNameLocal END) END)||' '|| (CASE WHEN fr.FatherLastNameLocal='' THEN fr.FatherLastName ELSE fr.FatherLastNameLocal END) AS FatherName,fr.EmailId,fr.Mobile,fr.Mobile1,fr.Mobile2,fr.BirthDate,fr.Gender,fr.BankAccountNo,fr.IFSCCode,fr.TotalAcreage,fr.FSSAINumber,fr.RegistrationDate,fr.ExpiryDate,ad.Street1,ad.Street2,ifnull(st.StateNameLocal,''),ifnull(dt.DistrictNameLocal,''),ifnull(bk.BlockNameLocal,''),ifnull(pc.PanchayatNameLocal,''),ifnull(vg.VillageNameLocal,''),ifnull(ct.CityNameLocal,''),pd.PinCode,ad.AddressType, lg.Name,ifnull(tp.TitleHindi,0),ifnull(irs.TitleHindi,0),ifnull(nr.TitleHindi,0),ifnull(nd.TitleHindi,0),ifnull(ws.TitleHindi,0),ifnull(es.TitleHindi,0), ifnull(ph.FileName,''), ifnull(pb.FileName,''), ifnull(fs.FileName,'')  FROM Farmer fr LEFT OUTER JOIN FarmerOtherDetails ot ON fr.FarmerUniqueId = ot.FarmerUniqueId LEFT OUTER JOIN SoilType tp ON ot.SoilTypeId = tp.Id LEFT OUTER JOIN NearestRiver nr ON ot.RiverId = nr.Id LEFT OUTER JOIN NearestDam nd ON ot.DamId = nd.Id LEFT OUTER JOIN IrrigationSystem irs ON ot.IrrigationSystemId = irs.Id LEFT OUTER JOIN WaterSource ws ON ot.WaterSourceId = ws.Id LEFT OUTER JOIN ElectricitySource es ON ot.ElectricitySourceId = es.Id LEFT OUTER JOIN (SELECT DISTINCT FarmerUniqueId,Type,FileName FROM FarmerDocuments) ph ON fr.FarmerUniqueId = ph.FarmerUniqueId AND ph.Type ='Farmer'  LEFT OUTER JOIN (SELECT DISTINCT FarmerUniqueId,Type,FileName FROM FarmerDocuments) pb ON fr.FarmerUniqueId = pb.FarmerUniqueId AND pb.Type ='PassBook'  LEFT OUTER JOIN (SELECT DISTINCT FarmerUniqueId,Type,FileName FROM FarmerDocuments) fs ON fr.FarmerUniqueId = fs.FarmerUniqueId AND fs.Type ='FSSAI', Address ad LEFT OUTER JOIN State st ON ad.StateId = st.StateId LEFT OUTER JOIN District dt ON ad.DistrictId = dt.DistrictId LEFT OUTER JOIN Block bk ON ad.BlockId = bk.BlockId LEFT OUTER JOIN Panchayat pc ON ad.PanchayatId = pc.PanchayatId LEFT OUTER JOIN Village vg ON ad.VillageId = vg.VillageId LEFT OUTER JOIN City ct ON ad.CityId = ct.CityId LEFT OUTER JOIN PinCode pd ON ad.PinCodeId = pd.PinCodeId, EducationLevel el, FarmerType ft, Languages lg  WHERE fr.FarmerUniqueId = ad.FarmerUniqueId AND el.EducationLevelId = fr.EducationLevelId AND ft.FarmerTypeId = fr.FarmerTypeId AND fr.LanguageId = lg.Id  AND fr.FarmerUniqueId ='" + farmerUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            farmerdetails.add(cursor.getString(0));
            farmerdetails.add(cursor.getString(1));
            farmerdetails.add(cursor.getString(2));
            farmerdetails.add(cursor.getString(3));
            farmerdetails.add(cursor.getString(4));
            farmerdetails.add(cursor.getString(5));
            farmerdetails.add(cursor.getString(6));
            farmerdetails.add(cursor.getString(7));
            farmerdetails.add(cursor.getString(8));
            farmerdetails.add(cursor.getString(9));
            farmerdetails.add(cursor.getString(10));
            farmerdetails.add(cursor.getString(11));
            farmerdetails.add(cursor.getString(12));
            farmerdetails.add(cursor.getString(13));
            farmerdetails.add(cursor.getString(14));
            farmerdetails.add(cursor.getString(15));
            farmerdetails.add(cursor.getString(16));
            farmerdetails.add(cursor.getString(17));
            farmerdetails.add(cursor.getString(18));
            farmerdetails.add(cursor.getString(19));
            farmerdetails.add(cursor.getString(20));
            farmerdetails.add(cursor.getString(21));
            farmerdetails.add(cursor.getString(22));
            farmerdetails.add(cursor.getString(23));
            farmerdetails.add(cursor.getString(24));
            farmerdetails.add(cursor.getString(25));
            farmerdetails.add(cursor.getString(26));
            farmerdetails.add(cursor.getString(27));
            farmerdetails.add(cursor.getString(28));
            farmerdetails.add(cursor.getString(29));
            farmerdetails.add(cursor.getString(30));
            farmerdetails.add(cursor.getString(31));
            farmerdetails.add(cursor.getString(32));
            farmerdetails.add(cursor.getString(33));
            farmerdetails.add(cursor.getString(34));
            farmerdetails.add(cursor.getString(35));
            farmerdetails.add(cursor.getString(36));
        }
        cursor.close();

        return farmerdetails;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get farmer details by unique id from Main table fro Updation">
    public ArrayList<String> getFarmerDetailsForUpdateUniqueId(String farmerUniqueId) {
        ArrayList<String> farmerdetails = new ArrayList<String>();
        String newFileName = "";
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT DISTINCT ifnull(fr.FarmerCode,'') AS FarmerCode,fr.EducationLevelId,ft.FarmerType,fr.Salutation||' '||fr.FirstName||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| fr.MiddleName END)||' '|| fr.LastName AS FarmerName,fr.FatherSalutation||' '||fr.FatherFirstName||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| fr.FatherMiddleName END)||' '|| fr.FatherLastName AS FatherName,fr.EmailId,fr.Mobile,fr.Mobile1,fr.Mobile2,fr.BirthDate,fr.Gender,fr.BankAccountNo,fr.IFSCCode,fr.TotalAcreage,fr.FSSAINumber,fr.RegistrationDate,fr.ExpiryDate,ad.Street1,ad.Street2,ifnull(st.StateName,''),ifnull(dt.DistrictName,''),ifnull(bk.BlockName,''),ifnull(pc.PanchayatName,''),ifnull(vg.VillageName,''),ifnull(ct.CityName,''),pd.PinCode,ad.AddressType, lg.Name, ifnull(ph.FileName,''), ifnull(pb.FileName,''), ifnull(fs.FileName,'')  FROM Farmer fr LEFT OUTER JOIN (SELECT DISTINCT FarmerUniqueId,Type,FileName FROM FarmerDocuments) ph ON fr.FarmerUniqueId = ph.FarmerUniqueId AND ph.Type ='Farmer'  LEFT OUTER JOIN (SELECT DISTINCT FarmerUniqueId,Type,FileName FROM FarmerDocuments) pb ON fr.FarmerUniqueId = pb.FarmerUniqueId AND pb.Type ='PassBook'  LEFT OUTER JOIN (SELECT DISTINCT FarmerUniqueId,Type,FileName FROM FarmerDocuments) fs ON fr.FarmerUniqueId = fs.FarmerUniqueId AND fs.Type ='FSSAI', Address ad LEFT OUTER JOIN State st ON ad.StateId = st.StateId LEFT OUTER JOIN District dt ON ad.DistrictId = dt.DistrictId LEFT OUTER JOIN Block bk ON ad.BlockId = bk.BlockId LEFT OUTER JOIN Panchayat pc ON ad.PanchayatId = pc.PanchayatId LEFT OUTER JOIN Village vg ON ad.VillageId = vg.VillageId LEFT OUTER JOIN City ct ON ad.CityId = ct.CityId LEFT OUTER JOIN PinCode pd ON ad.PinCodeId = pd.PinCodeId, FarmerType ft, Languages lg  WHERE fr.FarmerUniqueId = ad.FarmerUniqueId  AND ft.FarmerTypeId = fr.FarmerTypeId AND fr.LanguageId = lg.Id  AND fr.FarmerUniqueId ='" + farmerUniqueId + "' ";
        else
            selectQuery = "SELECT DISTINCT ifnull(fr.FarmerCode,'') AS FarmerCode,fr.EducationLevelId,ft.FarmerType,fr.SalutationLocal||' '||fr.FirstNameLocal||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| fr.MiddleNameLocal END)||' '|| fr.LastNameLocal AS FarmerName,fr.FatherSalutationLocal||' '||fr.FatherFirstNameLocal||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| fr.FatherMiddleNameLocal END)||' '|| fr.FatherLastNameLocal AS FatherName,fr.EmailId,fr.Mobile,fr.Mobile1,fr.Mobile2,fr.BirthDate,fr.Gender,fr.BankAccountNo,fr.IFSCCode,fr.TotalAcreage,fr.FSSAINumber,fr.RegistrationDate,fr.ExpiryDate,ad.Street1,ad.Street2,ifnull(st.StateNameLocal,''),ifnull(dt.DistrictNameLocal,''),ifnull(bk.BlockNameLocal,''),ifnull(pc.PanchayatNameLocal,''),ifnull(vg.VillageNameLocal,''),ifnull(ct.CityNameLocal,''),pd.PinCode,ad.AddressType, lg.Name, ifnull(ph.FileName,''), ifnull(pb.FileName,''), ifnull(fs.FileName,'')  FROM Farmer fr LEFT OUTER JOIN (SELECT DISTINCT FarmerUniqueId,Type,FileName FROM FarmerDocuments) ph ON fr.FarmerUniqueId = ph.FarmerUniqueId AND ph.Type ='Farmer'  LEFT OUTER JOIN (SELECT DISTINCT FarmerUniqueId,Type,FileName FROM FarmerDocuments) pb ON fr.FarmerUniqueId = pb.FarmerUniqueId AND pb.Type ='PassBook'  LEFT OUTER JOIN (SELECT DISTINCT FarmerUniqueId,Type,FileName FROM FarmerDocuments) fs ON fr.FarmerUniqueId = fs.FarmerUniqueId AND fs.Type ='FSSAI', Address ad LEFT OUTER JOIN State st ON ad.StateId = st.StateId LEFT OUTER JOIN District dt ON ad.DistrictId = dt.DistrictId LEFT OUTER JOIN Block bk ON ad.BlockId = bk.BlockId LEFT OUTER JOIN Panchayat pc ON ad.PanchayatId = pc.PanchayatId LEFT OUTER JOIN Village vg ON ad.VillageId = vg.VillageId LEFT OUTER JOIN City ct ON ad.CityId = ct.CityId LEFT OUTER JOIN PinCode pd ON ad.PinCodeId = pd.PinCodeId, FarmerType ft, Languages lg  WHERE fr.FarmerUniqueId = ad.FarmerUniqueId  AND ft.FarmerTypeId = fr.FarmerTypeId AND fr.LanguageId = lg.Id  AND fr.FarmerUniqueId ='" + farmerUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            farmerdetails.add(cursor.getString(0));
            farmerdetails.add(cursor.getString(1));
            farmerdetails.add(cursor.getString(2));
            farmerdetails.add(cursor.getString(3));
            farmerdetails.add(cursor.getString(4));
            farmerdetails.add(cursor.getString(5));
            farmerdetails.add(cursor.getString(6));
            farmerdetails.add(cursor.getString(7));
            farmerdetails.add(cursor.getString(8));
            farmerdetails.add(cursor.getString(9));
            farmerdetails.add(cursor.getString(10));
            farmerdetails.add(cursor.getString(11));
            farmerdetails.add(cursor.getString(12));
            farmerdetails.add(cursor.getString(13));
            farmerdetails.add(cursor.getString(14));
            farmerdetails.add(cursor.getString(15));
            farmerdetails.add(cursor.getString(16));
            farmerdetails.add(cursor.getString(17));
            farmerdetails.add(cursor.getString(18));
            farmerdetails.add(cursor.getString(19));
            farmerdetails.add(cursor.getString(20));
            farmerdetails.add(cursor.getString(21));
            farmerdetails.add(cursor.getString(22));
            farmerdetails.add(cursor.getString(23));
            farmerdetails.add(cursor.getString(24));
            farmerdetails.add(cursor.getString(25));
            farmerdetails.add(cursor.getString(26));
            farmerdetails.add(cursor.getString(27));
            farmerdetails.add(cursor.getString(28));
            farmerdetails.add(cursor.getString(29));
            farmerdetails.add(cursor.getString(30));
        }
        cursor.close();


        db.execSQL("DELETE FROM TempFarmerDocument WHERE FarmerUniqueId = '" + farmerUniqueId + "' AND Type IN ('FSSAI','PassBook') ");
        db.execSQL("INSERT INTO TempFarmerDocument(FarmerUniqueId,Type, FileName) SELECT FarmerUniqueId, Type,FileName FROM FarmerDocuments WHERE FarmerUniqueId = '" + farmerUniqueId + "' AND Type IN ('FSSAI','PassBook'); ");

        return farmerdetails;
    }
    //</editor-fold>

    //<editor-fold desc="To get all unsync New Farmer">
    public ArrayList<HashMap<String, String>> getUnSyncNewFarmer() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT fr.FarmerUniqueId,fr.EducationLevelId,fr. FarmerTypeId,fr. Salutation,fr. FirstName,fr. MiddleName,fr. LastName,fr.FatherSalutation,fr.FatherFirstName,fr.FatherMiddleName,fr.FatherLastName,fr.EmailId,fr.Mobile,fr.Mobile1,fr.Mobile2,fr.BirthDate,fr.Gender,fr.TotalAcreage,fr.LanguageId,fr.CreateBy,fr.CreateDate, ad.Street1,ad.Street2,ad.StateId,ad.DistrictId,ad.BlockId,ad.PanchayatId,ad.VillageId,ad.CityId,ad.PinCodeId,ad.AddressType,ifnull(od.SoilTypeId,0) AS SoilTypeId,ifnull(od.IrrigationSystemId,0) AS IrrigationSystemId,ifnull(od.RiverId,0) AS RiverId,ifnull(od.DamId,0) AS DamId,ifnull(od.WaterSourceId,0) AS WaterSourceId,ifnull(od.ElectricitySourceId,0) AS ElectricitySourceId, doc.FileName, fr.Longitude, fr.Latitude ,fr.Accuracy  FROM Farmer fr LEFT OUTER JOIN FarmerOtherDetails od ON fr.FarmerUniqueId = od.FarmerUniqueId, Address ad,(SELECT DISTINCT FarmerUniqueId,Type,FileName FROM FarmerDocuments) doc WHERE fr.FarmerCode IS NULL  AND fr.FarmerUniqueId = ad.FarmerUniqueId AND fr.FarmerUniqueId = doc.FarmerUniqueId AND doc.Type='Farmer' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmerUniqueId", cursor.getString(0));
            map.put("EducationLevelId", cursor.getString(1));
            map.put("FarmerTypeId", cursor.getString(2));
            map.put("Salutation", cursor.getString(3));
            map.put("FirstName", cursor.getString(4));
            map.put("MiddleName", cursor.getString(5));
            map.put("LastName", cursor.getString(6));
            map.put("FatherSalutation", cursor.getString(7));
            map.put("FatherFirstName", cursor.getString(8));
            map.put("FatherMiddleName", cursor.getString(9));
            map.put("FatherLastName", cursor.getString(10));
            map.put("EmailId", cursor.getString(11));
            map.put("Mobile", cursor.getString(12));
            map.put("Mobile1", cursor.getString(13));
            map.put("Mobile2", cursor.getString(14));
            map.put("BirthDate", cursor.getString(15));
            map.put("Gender", cursor.getString(16));
            map.put("TotalAcreage", cursor.getString(17));
            map.put("LanguageId", cursor.getString(18));
            map.put("CreateBy", cursor.getString(19));
            map.put("CreateDate", cursor.getString(20));
            map.put("Street1", cursor.getString(21));
            map.put("Street2", cursor.getString(22));
            map.put("StateId", cursor.getString(23));
            map.put("DistrictId", cursor.getString(24));
            map.put("BlockId", cursor.getString(25));
            map.put("PanchayatId", cursor.getString(26));
            map.put("VillageId", cursor.getString(27));
            map.put("CityId", cursor.getString(28));
            map.put("PinCodeId", cursor.getString(29));
            map.put("AddressType", cursor.getString(30));
            map.put("SoilTypeId", cursor.getString(31));
            map.put("IrrigationSystemId", cursor.getString(32));
            map.put("RiverId", cursor.getString(33));
            map.put("DamId", cursor.getString(34));
            map.put("WaterSourceId", cursor.getString(35));
            map.put("ElectricitySourceId", cursor.getString(36));
            map.put("PhotoFileName", cursor.getString(37));
            map.put("Longitude", cursor.getString(38));
            map.put("Latitude", cursor.getString(39));
            map.put("Accuracy", cursor.getString(40));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Block Assignment for New Farmer">
    public ArrayList<HashMap<String, String>> getUnSyncBlockAssignment() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT fob.FarmerUniqueId, fob.BlockId FROM FarmerOperatingBlocks fob, Farmer fr WHERE fob.FarmerUniqueId = fr.FarmerUniqueId AND fr.FarmerCode IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmerUniqueId", cursor.getString(0));
            map.put("BlockId", cursor.getString(1));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all FarmerUniqueId for Inserting Into Farmer Sync Table">
    public ArrayList<HashMap<String, String>> getFarmerUniqueUpdateId() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT FarmerUniqueId FROM FarmerSyncTable";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmerUniqueId", cursor.getString(0));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all FarmBlockUniqueId for Inserting Into Farm Block Sync Table">
    public ArrayList<HashMap<String, String>> getFarmBlockUniqueUpdateId() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT FarmBlockUniqueId FROM FarmBlockSyncTable";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmBlockUniqueId", cursor.getString(0));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all PlantationUniqueId for Inserting Into Plantation Sync Table">
    public ArrayList<HashMap<String, String>> getPlantationUniqueUpdateId() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT PlantationUniqueId FROM PlantationSyncTable";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("PlantationUniqueId", cursor.getString(0));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all PlantationUniqueId for Inserting Into InterCropping Sync Table">
    public ArrayList<HashMap<String, String>> getIntercroppingUniqueUpdateId() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();

        selectQuery = "SELECT InterCroppingUniqueId FROM InterCroppingSyncTable";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("InterCroppingUniqueId", cursor.getString(0));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all FarmerUniqueId for which Farmer Code is not Available">
    public ArrayList<HashMap<String, String>> getFarmerUniqueId() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT FarmerUniqueId,CreateBy FROM Farmer WHERE FarmerCode IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmerUniqueId", cursor.getString(0));
            map.put("CreateBy", cursor.getString(1));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all FarmBLockUniqueId for which Farm Block Code is not Available">
    public ArrayList<HashMap<String, String>> getFarmBlockUniqueId() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT FarmBlockUniqueId,CreateBy FROM FarmBlock WHERE FarmBlockCode IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmBlockUniqueId", cursor.getString(0));
            map.put("CreateBy", cursor.getString(1));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all PlantationUniqueId for which Plantation Code is not Available">
    public ArrayList<HashMap<String, String>> getPlantationUniqueId() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT PlantationUniqueId,CreateBy FROM FarmerPlantation WHERE PlantationCode IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("PlantationUniqueId", cursor.getString(0));
            map.put("CreateBy", cursor.getString(1));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Family Members by Farmer Unique Id">
    public List<FamilyMember> getFamilyMembers(String farmerUniqueId) {
        List<FamilyMember> labels = new ArrayList<FamilyMember>();
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT fm.Id,fm.MemberName,(CASE WHEN fm.Gender ='Male' THEN 'M' ELSE 'F' END),fm.BirthDate,rs.Title,fm.IsNominee,fm.NomineePercentage FROM FarmerFamilyMemberTemp fm, RelationShip rs WHERE fm.RelationShipId = rs.Id AND fm.FarmerUniqueId ='" + farmerUniqueId + "' ";
        else
            selectQuery = "SELECT fm.Id,fm.MemberName,(CASE WHEN fm.Gender ='Male' THEN 'M' ELSE 'F' END),fm.BirthDate,rs.TitleHindi,fm.IsNominee,fm.NomineePercentage FROM FarmerFamilyMemberTemp fm, RelationShip rs WHERE fm.RelationShipId = rs.Id AND fm.FarmerUniqueId ='" + farmerUniqueId + "' ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new FamilyMember(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Family Members From Main Table by Farmer Unique Id">
    public List<FamilyMember> getFamilyMemberDetails(String farmerUniqueId, String lang) {
        List<FamilyMember> labels = new ArrayList<FamilyMember>();

        if (lang.equalsIgnoreCase("en"))
            selectQuery = "SELECT DISTINCT fm.Id,fm.MemberName,(CASE WHEN fm.Gender ='Male' THEN 'M' ELSE 'F' END),fm.BirthDate,rs.Title,fm.IsNominee,fm.NomineePercentage FROM FarmerFamilyMember fm, RelationShip rs WHERE fm.RelationShipId = rs.Id AND fm.FarmerUniqueId ='" + farmerUniqueId + "' ";
        else
            selectQuery = "SELECT DISTINCT fm.Id,ifnull(fm.MemberNameLocal,fm.MemberName),(CASE WHEN fm.Gender ='Male' THEN 'M' ELSE 'F' END),fm.BirthDate,rs.TitleHindi,fm.IsNominee,fm.NomineePercentage FROM FarmerFamilyMember fm, RelationShip rs WHERE fm.RelationShipId = rs.Id AND fm.FarmerUniqueId ='" + farmerUniqueId + "' ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new FamilyMember(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list ofLoan Details by Farmer Unique Id">
    public List<LoanDetails> getLoanDetailsByFarmerUniqueId(String farmerUniqueId) {
        List<LoanDetails> labels = new ArrayList<LoanDetails>();
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT  t.Id,ls.Title, lt.Title, t.ROIPercentage, t.LoanAmount, t.BalanceAmount, t.Tenure FROM FarmerLoanDetailsTemp t, LoanSource ls, LoanType lt WHERE t.LoanSourceId = ls.Id AND t.LoanTypeId = lt.Id AND t.FarmerUniqueId ='" + farmerUniqueId + "' ";
        else
            selectQuery = "SELECT  t.Id,ls.TitleHindi, lt.TitleHindi, t.ROIPercentage, t.LoanAmount, t.BalanceAmount, t.Tenure FROM FarmerLoanDetailsTemp t, LoanSource ls, LoanType lt WHERE t.LoanSourceId = ls.Id AND t.LoanTypeId = lt.Id AND t.FarmerUniqueId ='" + farmerUniqueId + "' ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new LoanDetails(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Asset Details by Farmer Unique Id">
    public List<FarmerAssetData> getAssetDetailsByFarmerUniqueId(String farmerUniqueId) {
        List<FarmerAssetData> labels = new ArrayList<FarmerAssetData>();
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT fa.Id, fa.Title, ifnull(fad.FarmAssetsNo,'') FROM FarmAsset fa LEFT OUTER JOIN FarmerAssetDetails fad ON fa.Id = fad.FarmAssetsId AND fad.FarmerUniqueId ='" + farmerUniqueId + "'  ORDER BY Title COLLATE NOCASE ASC ";
        else
            selectQuery = "SELECT fa.Id, fa.TitleHindi, ifnull(fad.FarmAssetsNo,'') FROM FarmAsset fa LEFT OUTER JOIN FarmerAssetDetails fad ON fa.Id = fad.FarmAssetsId AND fad.FarmerUniqueId ='" + farmerUniqueId + "'  ORDER BY Title COLLATE NOCASE ASC ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new FarmerAssetData(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get farm block details by farm block unique id">
    public List<FarmBlockViewData> getFarmBlockById(String farmBlockUniqueId) {
        List<FarmBlockViewData> labels = new ArrayList<FarmBlockViewData>();
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT DISTINCT FarmBlockCode, FarmerId, fb.FPOId, o.Title AS fpo, a.Street1, a.Street2, a.StateId,st.StateName, a.DistrictId, di.DistrictName, a.BlockId, bl.BlockName, a.PanchayatId, pn.PanchayatName, a.VillageId,vi.VillageName, a.PinCodeId, pi.PinCode, a.AddressType, fb.KhataNo, fb.KhasraNo, fb.ContractDate,fb.Acerage,ifnull(fb.SoilTypeId,0), ifnull(so.Title,'') AS SoilType, fb.ElevationMSL, fb.PHChemical, fb.Nitrogen,fb.Potash, fb.Phosphorus, fb.OrganicCarbonPerc, fb.Magnesium, fb.Calcium, ifnull(fb.ExistingUseId,0), ifnull(eu.Title,'') AS ExistingUse,ifnull(fb.CommunityUseId,0), ifnull(cu.Title ,'') AS CommunityUse, ifnull(eh.Title,'') AS ExistingHazard,ifnull(fb.ExistingHazardId,0), ifnull(fb.RiverId,0), ifnull(nr.Title,'') AS River, ifnull(fb.DamId,0), ifnull(nd.Title,'') AS Dam, ifnull(fb.IrrigationId,0), ifnull(ir.Title,'') AS Irrigation, fb.OverheadTransmission, ifnull(fb.LegalDisputeId,0), ifnull(ld.Title,'') AS LegalDispute, ifnull(fb.SourceWaterId,0), ifnull(ws.Title,'') AS SourceWater, ifnull(fb.ElectricitySourceId,0), ifnull(es.title,'') AS ElectricitySource, fb.DripperSpacing, fb.DischargeRate, ifnull(fb.LandTypeId,0), ifnull(lt.Title,'') AS LandType, fb.OwnerName, fb.OwnerMobile FROM FarmBlock fb LEFT OUTER JOIN Organizer o ON fb.FPOId = o.Id LEFT OUTER JOIN SoilType so ON fb.SoilTypeId = so.Id LEFT OUTER JOIN ExistingUse eu ON fb.ExistingUseId =eu.Id LEFT OUTER JOIN CommunityUse cu ON fb.CommunityUseId = cu.Id LEFT OUTER JOIN ExistingHazard eh ON fb.ExistingHazardId = eh.Id LEFT OUTER JOIN NearestRiver nr ON fb.RiverId = nr.Id LEFT OUTER JOIN NearestDam nd ON fb.DamId = nd.Id LEFT OUTER JOIN IrrigationSystem ir ON fb.IrrigationId = ir.Id LEFT OUTER JOIN LegalDispute ld ON fb.LegalDisputeId = ld.Id LEFT OUTER JOIN WaterSource ws ON fb.SourceWaterId = ws.id LEFT OUTER JOIN ElectricitySource es ON fb.ElectricitySourceId = es.Id LEFT OUTER JOIN LandType lt ON fb.LandTypeId = lt.Id, Address a, State st, District di, Block bl, Panchayat pn, Village vi, PinCode pi  WHERE a.FarmerUniqueId = fb.FarmBlockUniqueId AND st.StateId = a.StateId AND a.DistrictId = di.DistrictId AND a.BlockId = bl.BlockId AND a.PanchayatId = pn.PanchayatId AND a.VillageId = vi.VillageId AND a.PinCodeId = pi.PinCodeId AND FarmBlockUniqueId =  '" + farmBlockUniqueId + "' ";
        else
            selectQuery = "SELECT DISTINCT FarmBlockCode, FarmerId, fb.FPOId, o.Title AS fpo, a.Street1, a.Street2, a.StateId,st.StateNameLocal, a.DistrictId, di.DistrictNameLocal, a.BlockId, bl.BlockNameLocal, a.PanchayatId, pn.PanchayatNameLocal, a.VillageId,vi.VillageNameLocal, a.PinCodeId, pi.PinCode, a.AddressType, fb.KhataNo, fb.KhasraNo, fb.ContractDate,fb.Acerage,ifnull(fb.SoilTypeId,0), ifnull(so.TitleHindi,'') AS SoilType, fb.ElevationMSL, fb.PHChemical, fb.Nitrogen,fb.Potash, fb.Phosphorus, fb.OrganicCarbonPerc, fb.Magnesium, fb.Calcium, ifnull(fb.ExistingUseId,0), ifnull(eu.Title,'') AS ExistingUse,ifnull(fb.CommunityUseId,0), ifnull(cu.TitleHindi ,'') AS CommunityUse, ifnull(eh.TitleHindi,'') AS ExistingHazard,ifnull(fb.ExistingHazardId,0), ifnull(fb.RiverId,0), ifnull(nr.TitleHindi,'') AS River, ifnull(fb.DamId,0), ifnull(nd.TitleHindi,'') AS Dam, ifnull(fb.IrrigationId,0), ifnull(ir.TitleHindi,'') AS Irrigation, fb.OverheadTransmission, ifnull(fb.LegalDisputeId,0), ifnull(ld.TitleHindi,'') AS LegalDispute, ifnull(fb.SourceWaterId,0), ifnull(ws.TitleHindi,'') AS SourceWater, ifnull(fb.ElectricitySourceId,0), ifnull(es.titleHindi,'') AS ElectricitySource, fb.DripperSpacing, fb.DischargeRate, ifnull(fb.LandTypeId,0), ifnull(lt.TitleHindi,'') AS LandType, fb.OwnerName, fb.OwnerMobile FROM FarmBlock fb LEFT OUTER JOIN Organizer o ON fb.FPOId = o.Id LEFT OUTER JOIN SoilType so ON fb.SoilTypeId = so.Id LEFT OUTER JOIN ExistingUse eu ON fb.ExistingUseId =eu.Id LEFT OUTER JOIN CommunityUse cu ON fb.CommunityUseId = cu.Id LEFT OUTER JOIN ExistingHazard eh ON fb.ExistingHazardId = eh.Id LEFT OUTER JOIN NearestRiver nr ON fb.RiverId = nr.Id LEFT OUTER JOIN NearestDam nd ON fb.DamId = nd.Id LEFT OUTER JOIN IrrigationSystem ir ON fb.IrrigationId = ir.Id LEFT OUTER JOIN LegalDispute ld ON fb.LegalDisputeId = ld.Id LEFT OUTER JOIN WaterSource ws ON fb.SourceWaterId = ws.id LEFT OUTER JOIN ElectricitySource es ON fb.ElectricitySourceId = es.Id LEFT OUTER JOIN LandType lt ON fb.LandTypeId = lt.Id, Address a, State st, District di, Block bl, Panchayat pn, Village vi, PinCode pi  WHERE a.FarmerUniqueId = fb.FarmBlockUniqueId AND st.StateId = a.StateId AND a.DistrictId = di.DistrictId AND a.BlockId = bl.BlockId AND a.PanchayatId = pn.PanchayatId AND a.VillageId = vi.VillageId AND a.PinCodeId = pi.PinCodeId AND FarmBlockUniqueId =  '" + farmBlockUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new FarmBlockViewData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18), cursor.getString(19), cursor.getString(20), cursor.getString(21), cursor.getString(22), cursor.getString(23), cursor.getString(24), cursor.getString(25), cursor.getString(26), cursor.getString(27), cursor.getString(28), cursor.getString(29), cursor.getString(30), cursor.getString(31), cursor.getString(32), cursor.getString(33), cursor.getString(34), cursor.getString(35), cursor.getString(36), cursor.getString(37), cursor.getString(38), cursor.getString(39), cursor.getString(40), cursor.getString(41), cursor.getString(42), cursor.getString(43), cursor.getString(44), cursor.getString(45), cursor.getString(46), cursor.getString(47), cursor.getString(48), cursor.getString(49), cursor.getString(50), cursor.getString(51), cursor.getString(52), cursor.getString(53), cursor.getString(54), cursor.getString(55), cursor.getString(56), cursor.getString(57)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Farm Block for Display">
    public List<FarmBlockData> getFarmBlockList(String farmerUniqueId) {
        List<FarmBlockData> labels = new ArrayList<FarmBlockData>();
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT DISTINCT fb.FarmBlockUniqueId, ifnull(fb.FarmBlockCode,'') AS FarmBlockCode, fb.Acerage||' Acre',fb.ContractDate,fb.KhataNo,fb.KhasraNo, (CASE WHEN ad.AddressType ='District Based' THEN vi.VillageName||' , '||bl.BlockName ELSE ci.CityName ||' , '|| di.DistrictName END) AS Address FROM FarmBlock fb, Address ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId WHERE fb.FarmBlockUniqueId = ad.FarmerUniqueId AND fb.FarmerId ='" + farmerUniqueId + "'";
        else
            selectQuery = "SELECT DISTINCT fb.FarmBlockUniqueId, ifnull(fb.FarmBlockCode,'') AS FarmBlockCode, fb.Acerage||' Acre',fb.ContractDate,fb.KhataNo,fb.KhasraNo, (CASE WHEN ad.AddressType ='District Based' THEN vi.VillageNameLocal||' , '||bl.BlockNameLocal ELSE ci.CityNameLocal ||' , '|| di.DistrictNameLocal END) AS Address FROM FarmBlock fb, Address ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId WHERE fb.FarmBlockUniqueId = ad.FarmerUniqueId AND fb.FarmerId ='" + farmerUniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new FarmBlockData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="To get all unsync Farmer with Farmer Code">
    public ArrayList<HashMap<String, String>> getUnSyncFarmers() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT fr.FarmerUniqueId,fr.EducationLevelId,fr.EmailId,fr.Mobile1,fr.Mobile2,fr.TotalAcreage,fr.BankAccountNo, fr.IFSCCode,fr.FSSAINumber,fr.RegistrationDate,fr.ExpiryDate, fr.CreateBy,fr.CreateDate, fr.Longitude, fr.Latitude ,fr.Accuracy, ifnull(bk.FileName,'') AS BankFile, ifnull(fs.FileName,'') AS FSSAIFile, (CASE WHEN fr.IsLoanNotApplicable = '' THEN 0 WHEN fr.IsLoanNotApplicable IS NULL THEN 0 ELSE fr.IsLoanNotApplicable END) AS IsLoanNA  FROM Farmer fr LEFT OUTER JOIN FarmerDocuments bk ON fr.FarmerUniqueId = bk.FarmerUniqueId AND bk.Type ='PassBook' LEFT OUTER JOIN FarmerDocuments fs ON fr.FarmerUniqueId = fs.FarmerUniqueId AND fs.Type ='FSSAI' WHERE fr.FarmerCode IS NOT NULL AND fr.IsSync ='' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmerUniqueId", cursor.getString(0));
            map.put("EducationLevelId", cursor.getString(1));
            map.put("EmailId", cursor.getString(2));
            map.put("Mobile1", cursor.getString(3));
            map.put("Mobile2", cursor.getString(4));
            map.put("TotalAcreage", cursor.getString(5));
            map.put("BankAccountNo", cursor.getString(6));
            map.put("IFSCCode", cursor.getString(7));
            map.put("FSSAINumber", cursor.getString(8));
            map.put("RegistrationDate", cursor.getString(9));
            map.put("ExpiryDate", cursor.getString(10));
            map.put("BankFileName", cursor.getString(16));
            map.put("FSSAIFileName", cursor.getString(17));
            map.put("CreateBy", cursor.getString(11));
            map.put("CreateDate", cursor.getString(12));
            map.put("Longitude", cursor.getString(13));
            map.put("Latitude", cursor.getString(14));
            map.put("Accuracy", cursor.getString(15));
            map.put("IsLoanNA", cursor.getString(18));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Farmer Family Details for Farmers">
    public ArrayList<HashMap<String, String>> getUnSyncFamilyDetails() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT FarmerUniqueId, MemberName,Gender,BirthDate,RelationShipId,IsNominee,(CASE WHEN NomineePercentage='' THEN 0 ELSE NomineePercentage END) AS NomineePercentage FROM FarmerFamilyMember WHERE IsSync IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmerUniqueId", cursor.getString(0));
            map.put("MemberName", cursor.getString(1));
            map.put("Gender", cursor.getString(2));
            map.put("BirthDate", cursor.getString(3));
            map.put("RelationShipId", cursor.getString(4));
            map.put("IsNominee", cursor.getString(5));
            map.put("NomineePercentage", cursor.getString(6));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Farmer Loan Details for Farmers">
    public ArrayList<HashMap<String, String>> getUnSyncLoanDetails() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT FarmerUniqueId, LoanSourceId, LoanTypeId, ROIPercentage,LoanAmount, BalanceAmount, Tenure FROM FarmerLoanDetails WHERE IsSync IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmerUniqueId", cursor.getString(0));
            map.put("LoanSourceId", cursor.getString(1));
            map.put("LoanTypeId", cursor.getString(2));
            map.put("ROIPercentage", cursor.getString(3));
            map.put("LoanAmount", cursor.getString(4));
            map.put("BalanceAmount", cursor.getString(5));
            map.put("Tenure", cursor.getString(6));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Farmer Asset Details for Farmers">
    public ArrayList<HashMap<String, String>> getUnSyncAssetDetails() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT FarmerUniqueId, FarmAssetsId, FarmAssetsNo FROM FarmerAssetDetails WHERE IsSync IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmerUniqueId", cursor.getString(0));
            map.put("FarmAssetsId", cursor.getString(1));
            map.put("FarmAssetsNo", cursor.getString(2));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Farmer Proof Details for Farmers">
    public ArrayList<HashMap<String, String>> getUnSyncProofDetails() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT fp.FarmerUniqueId, fp.UniqueId,fp.POAPOIId, fp.DocumentNo,fp.FileName FROM FarmerProof fp, Farmer fr  WHERE fp.FarmerUniqueId = fr.FarmerUniqueId AND (fr.IsSync IS NULL OR fr.IsSync ='')";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmerUniqueId", cursor.getString(0));
            map.put("UniqueId", cursor.getString(1));
            map.put("POAPOIId", cursor.getString(2));
            map.put("DocumentNo", cursor.getString(3));
            map.put("FileName", cursor.getString(4));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Block Assignment for Old Farmer">
    public ArrayList<HashMap<String, String>> getUnSyncBlockAssignmentOld() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT fob.FarmerUniqueId, fob.BlockId FROM FarmerOperatingBlocks fob, Farmer fr WHERE fob.FarmerUniqueId = fr.FarmerUniqueId AND fr.FarmerCode IS NOT NULL";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmerUniqueId", cursor.getString(0));
            map.put("BlockId", cursor.getString(1));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Attachments for Farmer">
    public ArrayList<HashMap<String, String>> getAttachmentsForSync() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT DISTINCT FarmerUniqueId, Type,FileName AS FilePath, FileName FROM FarmerDocuments WHERE IsSync IS NULL AND FileName IS NOT NULL AND FileName !='' UNION ALL SELECT DISTINCT  FarmerUniqueId, 'Proof' AS Type, FilePath, FileName FROM FarmerProof WHERE IsSync IS NULL AND FileName IS NOT NULL AND FilePath IS NOT NULL AND FileName !='' AND FilePath !='' UNION ALL SELECT DISTINCT  UniqueId, 'VisitReport' AS Type, FilePath, FileName FROM VisitReportPhoto WHERE IsSync IS NULL AND IsTemp = '0'  AND FileName IS NOT NULL AND FilePath IS NOT NULL AND FileName !='' AND FilePath !='' UNION ALL SELECT DISTINCT  UniqueId, 'Recommendation' AS Type, FileName, FileName FROM RecommendationDetail WHERE IsSyncFile IS NULL AND IsTemp = '0' AND FileName !='' UNION ALL SELECT UniqueId,'JobCard' AS Type, FileName AS FilePath, FileName FROM JobCardDetail WHERE (IsSync IS NULL OR IsSync ='') AND FileName !='' AND FileName IS NOT NULL";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            //map.put("DocumentId", cursor.getString(0));
            map.put("FarmerUniqueId", cursor.getString(0));
            map.put("Type", cursor.getString(1));
            map.put("FilePath", cursor.getString(2));
            map.put("FileName", cursor.getString(3));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>


    //<editor-fold desc="Method to Update Attachment Sync Status">

    public void updateAttachmentStatus(String docId, String docType) {
        if (docType.equalsIgnoreCase("Proof"))
            selectQuery = "UPDATE FarmerProof SET IsSync = '1' WHERE FilePath ='" + docId + "'";
        else if (docType.equalsIgnoreCase("VisitReport"))
            selectQuery = "UPDATE VisitReportPhoto SET IsSync = '1' WHERE FilePath ='" + docId + "'";
        else if (docType.equalsIgnoreCase("Recommendation"))
            selectQuery = "UPDATE RecommendationDetail SET IsSyncFile = '1' WHERE FileName ='" + docId + "'";
        else
            selectQuery = "UPDATE FarmerDocuments SET IsSync = '1' WHERE FileName ='" + docId + "'";
        db.execSQL(selectQuery);
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Loan Details From Main Table by Farmer Unique Id">
    public List<LoanDetails> getLoanDetailsFromMainByFarmerUniqueId(String farmerUniqueId, String lang) {
        List<LoanDetails> labels = new ArrayList<LoanDetails>();
        if (lang.equalsIgnoreCase("en"))
            selectQuery = "SELECT DISTINCT t.Id,ls.Title, lt.Title, t.ROIPercentage, t.LoanAmount, t.BalanceAmount, t.Tenure FROM FarmerLoanDetails t, LoanSource ls, LoanType lt WHERE t.LoanSourceId = ls.Id AND t.LoanTypeId = lt.Id AND t.FarmerUniqueId ='" + farmerUniqueId + "' ";
        else
            selectQuery = "SELECT DISTINCT t.Id,ls.TitleHindi, lt.TitleHindi, t.ROIPercentage, t.LoanAmount, t.BalanceAmount, t.Tenure FROM FarmerLoanDetails t, LoanSource ls, LoanType lt WHERE t.LoanSourceId = ls.Id AND t.LoanTypeId = lt.Id AND t.FarmerUniqueId ='" + farmerUniqueId + "' ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new LoanDetails(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Asset Details From Main Table by Farmer Unique Id">
    public List<FarmerAssetData> getAssetDetailsFromMainByFarmerUniqueId(String farmerUniqueId, String lang) {
        List<FarmerAssetData> labels = new ArrayList<FarmerAssetData>();
        if (lang.equalsIgnoreCase("en"))
            selectQuery = "SELECT DISTINCT fa.Id, fa.Title, ifnull(fad.FarmAssetsNo,'') FROM FarmAsset fa, FarmerAssetDetails fad WHERE fa.Id = fad.FarmAssetsId AND fad.FarmerUniqueId ='" + farmerUniqueId + "'  ORDER BY Title COLLATE NOCASE ASC ";
        else
            selectQuery = "SELECT DISTINCT fa.Id, fa.TitleHindi, ifnull(fad.FarmAssetsNo,'') FROM FarmAsset fa, FarmerAssetDetails fad WHERE fa.Id = fad.FarmAssetsId AND fad.FarmerUniqueId ='" + farmerUniqueId + "'  ORDER BY Title COLLATE NOCASE ASC ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new FarmerAssetData(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Operational Blocks from Main Table by Farmer Unique Id">
    public List<OperationalBlocks> getOperationalDistrictMain(String farmerUniqueId, String lang) {
        List<OperationalBlocks> labels = new ArrayList<OperationalBlocks>();
        if (lang.equalsIgnoreCase("en"))
            selectQuery = "SELECT DISTINCT t.Id, d.DistrictName, b.BlockName FROM FarmerOperatingBlocks t, District d, Block b WHERE t.DistrictId = d.DistrictId AND t.BlockId = b.BlockId AND t.FarmerUniqueId = '" + farmerUniqueId + "' ";
        else
            selectQuery = "SELECT DISTINCT t.Id, d.DistrictNameLocal, b.BlockNameLocal FROM FarmerOperatingBlocks t, District d, Block b WHERE t.DistrictId = d.DistrictId AND t.BlockId = b.BlockId AND t.FarmerUniqueId = '" + farmerUniqueId + "' ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new OperationalBlocks(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get Primary Farm Block Details by unique id">
    public ArrayList<String> getPrimaryFarmBlockDetailsByUniqueId(String farmBlockUniqueId) {
        ArrayList<String> farmBlockdetails = new ArrayList<String>();
        String newFileName = "";
        selectQuery = "SELECT fb.FarmBlockUniqueId,ifnull(fb.FarmBlockCode,''),fb.LandTypeId,fb.FPOId,fb.KhataNo,fb.KhasraNo,fb.ContractDate,fb.Acerage, ad.Street1,ad.Street2,ad.StateId,ad.DistrictId,ad.BlockId,ad.PanchayatId,ad.VillageId,ad.PinCodeId,ad.AddressType, fb.OwnerName, fb.OwnerMobile FROM FarmBlock fb, Address ad WHERE fb.FarmBlockUniqueId = ad.FarmerUniqueId AND fb.FarmBlockUniqueId ='" + farmBlockUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            farmBlockdetails.add(cursor.getString(0));
            farmBlockdetails.add(cursor.getString(1));
            farmBlockdetails.add(cursor.getString(2));
            farmBlockdetails.add(cursor.getString(3));
            farmBlockdetails.add(cursor.getString(4));
            farmBlockdetails.add(cursor.getString(5));
            farmBlockdetails.add(cursor.getString(6));
            farmBlockdetails.add(cursor.getString(7));
            farmBlockdetails.add(cursor.getString(8));
            farmBlockdetails.add(cursor.getString(9));
            farmBlockdetails.add(cursor.getString(10));
            farmBlockdetails.add(cursor.getString(11));
            farmBlockdetails.add(cursor.getString(12));
            farmBlockdetails.add(cursor.getString(13));
            farmBlockdetails.add(cursor.getString(14));
            farmBlockdetails.add(cursor.getString(15));
            farmBlockdetails.add(cursor.getString(16));
            farmBlockdetails.add(cursor.getString(17));
            farmBlockdetails.add(cursor.getString(18));
        }
        cursor.close();

        return farmBlockdetails;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get Soil Details for Farm Block by unique id">
    public ArrayList<String> getSoilDetailsOfFarmBlockByUniqueId(String farmBlockUniqueId) {
        ArrayList<String> farmBlockdetails = new ArrayList<String>();
        String newFileName = "";
        selectQuery = "SELECT ifnull(SoilTypeId,0),ifnull(ElevationMSL,''),ifnull(PHChemical,''),ifnull(Nitrogen,''),ifnull(Potash,''),ifnull(Phosphorus,''),ifnull(OrganicCarbonPerc,''),ifnull(Magnesium,''),ifnull(Calcium,'') FROM FarmBlock WHERE FarmBlockUniqueId ='" + farmBlockUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            farmBlockdetails.add(cursor.getString(0));
            farmBlockdetails.add(cursor.getString(1));
            farmBlockdetails.add(cursor.getString(2));
            farmBlockdetails.add(cursor.getString(3));
            farmBlockdetails.add(cursor.getString(4));
            farmBlockdetails.add(cursor.getString(5));
            farmBlockdetails.add(cursor.getString(6));
            farmBlockdetails.add(cursor.getString(7));
            farmBlockdetails.add(cursor.getString(8));
        }
        cursor.close();

        return farmBlockdetails;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get Other Details for Farm Block by unique id">
    public ArrayList<String> getOtherDetailsOfFarmBlockByUniqueId(String farmBlockUniqueId) {
        ArrayList<String> farmBlockdetails = new ArrayList<String>();
        String newFileName = "";
        selectQuery = "SELECT ifnull(ExistingUseId,0),ifnull(CommunityUseId,0),ifnull(ExistingHazardId,0),ifnull(RiverId,0),ifnull(DamId,0),ifnull(IrrigationId,0),ifnull(OverheadTransmission,''),ifnull(LegalDisputeId,0),ifnull(SourceWaterId,0),ifnull(ElectricitySourceId,0),ifnull(DripperSpacing,''),ifnull(DischargeRate,'') FROM FarmBlock WHERE FarmBlockUniqueId ='" + farmBlockUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            farmBlockdetails.add(cursor.getString(0));
            farmBlockdetails.add(cursor.getString(1));
            farmBlockdetails.add(cursor.getString(2));
            farmBlockdetails.add(cursor.getString(3));
            farmBlockdetails.add(cursor.getString(4));
            farmBlockdetails.add(cursor.getString(5));
            farmBlockdetails.add(cursor.getString(6));
            farmBlockdetails.add(cursor.getString(7));
            farmBlockdetails.add(cursor.getString(8));
            farmBlockdetails.add(cursor.getString(9));
            farmBlockdetails.add(cursor.getString(10));
            farmBlockdetails.add(cursor.getString(11));
        }
        cursor.close();

        return farmBlockdetails;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Land Characteristic Details by Farm Block Unique Id">
    public List<LandCharacteristic> getLandCharacteristicByFarmBlockUniqueId(String farmBlockUniqueId) {
        List<LandCharacteristic> labels = new ArrayList<LandCharacteristic>();
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT lc.Id, lc.Title, ifnull(flc.LandCharacteristicId,0) FROM LandCharacteristic lc LEFT OUTER JOIN FarmBlockLandCharacteristic flc ON lc.Id = flc.LandCharacteristicId AND flc.FarmBlockUniqueId ='" + farmBlockUniqueId + "' ORDER BY lc.Title  COLLATE NOCASE ASC";
        else
            selectQuery = "SELECT lc.Id, lc.TitleHindi, ifnull(flc.LandCharacteristicId,0) FROM LandCharacteristic lc LEFT OUTER JOIN FarmBlockLandCharacteristic flc ON lc.Id = flc.LandCharacteristicId AND flc.FarmBlockUniqueId ='" + farmBlockUniqueId + "' ORDER BY lc.Title  COLLATE NOCASE ASC";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new LandCharacteristic(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Land Issue Details by Farm Block Unique Id">
    public List<LandCharacteristic> getLandIssueByFarmBlockUniqueId(String farmBlockUniqueId) {
        List<LandCharacteristic> labels = new ArrayList<LandCharacteristic>();
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT li.Id, li.Title, ifnull(fli.LandIssueId,0) FROM LandIssue li LEFT OUTER JOIN FarmBlockLandIssue fli ON li.Id = fli.LandIssueId AND fli.FarmBlockUniqueId ='" + farmBlockUniqueId + "' ORDER BY li.Title  COLLATE NOCASE ASC ";
        else
            selectQuery = "SELECT li.Id, li.TitleHindi, ifnull(fli.LandIssueId,0) FROM LandIssue li LEFT OUTER JOIN FarmBlockLandIssue fli ON li.Id = fli.LandIssueId AND fli.FarmBlockUniqueId ='" + farmBlockUniqueId + "' ORDER BY li.Title  COLLATE NOCASE ASC ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new LandCharacteristic(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if Coordinates are already Added in Temporary Table">
    public Boolean TempGPSExists(String latitude, String longitude) {
        Boolean dataExists = false;

        selectQuery = "SELECT * FROM TempGPS WHERE Latitude = '" + latitude + "' AND Longitude ='" + longitude + "'  ";

        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get Coordinates from Temporary Table">
    public List<CustomCoord> GetTempGPS() {
        List<CustomCoord> gps = new ArrayList<CustomCoord>();
        selectQuery = "SELECT Id, Latitude, Longitude,Accuracy FROM TempGPS ORDER BY Id";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            gps.add(new CustomCoord(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }
        cursor.close();
        return gps;

    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Farm Block Cropping Pattern by Farm Block Unique Id">
    public List<FarmBlockCroppingPatternData> getFarmBlockCroppingPatternByUniqueId(String farmBlockUniqueId) {
        List<FarmBlockCroppingPatternData> labels = new ArrayList<FarmBlockCroppingPatternData>();
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT  DISTINCT t.Id, t.farmBlockUniqueId, t.CropId, c.Title AS Crop, t.CropVarietyId, v.Title AS CropVariety, t.SeasonId, s.Title AS Season, t.acreage FROM FarmBlockCroppingPattern t, Crop c, Variety v, Season s WHERE t.CropId = c.Id AND t.CropVarietyId = v.Id AND t.SeasonId = s.Id AND t.FarmBlockUniqueId ='" + farmBlockUniqueId + "' ";
        else
            selectQuery = "SELECT  DISTINCT t.Id, t.farmBlockUniqueId, t.CropId, c.TitleHindi AS Crop, t.CropVarietyId, v.TitleHindi AS CropVariety, t.SeasonId, s.TitleHindi AS Season, t.acreage FROM FarmBlockCroppingPattern t, Crop c, Variety v, Season s WHERE t.CropId = c.Id AND t.CropVarietyId = v.Id AND t.SeasonId = s.Id AND t.FarmBlockUniqueId ='" + farmBlockUniqueId + "' ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new FarmBlockCroppingPatternData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if variety and season already added">
    public Boolean isVarietySeasonAlreadyAdded(String farmBlockUniqueId, String varietyId, String seasonId) {
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM FarmBlockCroppingPattern WHERE  FarmBlockUniqueId = '" + farmBlockUniqueId + "' AND CropVarietyId = '" + varietyId + "' AND SeasonId ='" + seasonId + "' ";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="To get all New Farm Blocks For Sync">
    public ArrayList<HashMap<String, String>> getNewFarmBlocks() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> wordCharacteristic = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> wordIssue = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT fb.FarmBlockUniqueId,fb.FarmerId,ifnull(fb.LandTypeId,0),ifnull(fb.FPOId,0),fb.KhataNo,fb.KhasraNo,(CASE WHEN fb.ContractDate='' THEN '1900-01-01' ELSE fb.ContractDate END),fb.Acerage,ifnull(fb.SoilTypeId,0),ifnull((CASE WHEN fb.ElevationMSL='' THEN 0 ELSE fb.ElevationMSL END),0),ifnull((CASE WHEN fb.PHChemical='' THEN 0 ELSE fb.PHChemical END) ,0),ifnull((CASE WHEN fb.Nitrogen='' THEN 0 ELSE fb.Nitrogen END),0),ifnull((CASE WHEN fb.Potash='' THEN 0 ELSE fb.Potash END),0) ,ifnull((CASE WHEN fb.Phosphorus='' THEN 0 ELSE fb.Phosphorus END),0),ifnull((CASE WHEN fb.OrganicCarbonPerc='' THEN 0 ELSE fb.OrganicCarbonPerc END),0),ifnull((CASE WHEN fb.Magnesium='' THEN 0 ELSE fb.Magnesium END),0),ifnull((CASE WHEN fb.Calcium='' THEN 0 ELSE fb.Calcium END),0),ifnull(fb.ExistingUseId,0),ifnull(fb.CommunityUseId,0) ,ifnull(fb.ExistingHazardId,0),ifnull(fb.RiverId,0),ifnull(fb.DamId,0),ifnull(fb.IrrigationId,0),fb.OverheadTransmission,ifnull(fb.LegalDisputeId,0) ,ifnull(fb.SourceWaterId,0),ifnull(fb.ElectricitySourceId,0),ifnull((CASE WHEN fb.DripperSpacing='' THEN 0 ELSE fb.DripperSpacing END),0),ifnull((CASE WHEN fb.DischargeRate='' THEN 0 ELSE fb.DischargeRate END),0),fb.CreateBy,fb.CreateDate,fb.Longitude,fb.Latitude,fb.Accuracy,ad.Street1,ad.Street2,ad.StateId,ad.DistrictId,ad.BlockId,ad.PanchayatId,ad.VillageId,ad.PinCodeId,ad.AddressType, fb.OwnerName, fb.OwnerMobile FROM FarmBlock fb, Address ad WHERE fb.FarmBlockCode IS NULL AND fb.FarmBlockUniqueId = ad.FarmerUniqueId";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            String newFarmBlockUniqueId = "";
            map.put("FarmBlockUniqueId", cursor.getString(0));
            map.put("FarmerUniqueId", cursor.getString(1));
            map.put("LandTypeId", cursor.getString(2));
            map.put("FPOId", cursor.getString(3));
            map.put("KhataNo", cursor.getString(4));
            map.put("KhasraNo", cursor.getString(5));
            map.put("ContractDate", cursor.getString(6));
            map.put("Acerage", cursor.getString(7));
            map.put("SoilTypeId", cursor.getString(8));
            map.put("ElevationMSL", cursor.getString(9));
            map.put("SoliPH", cursor.getString(10));
            map.put("Nitrogen", cursor.getString(11));
            map.put("Potash", cursor.getString(12));
            map.put("Phosphorus", cursor.getString(13));
            map.put("OrganicCarbonPerc", cursor.getString(14));
            map.put("Magnesium", cursor.getString(15));
            map.put("Calcium", cursor.getString(16));
            map.put("ExistingUseId", cursor.getString(17));
            map.put("CommunityUseId", cursor.getString(18));
            map.put("ExistingHazardId", cursor.getString(19));
            map.put("RiverId", cursor.getString(20));
            map.put("DamId", cursor.getString(21));
            map.put("IrrigationId", cursor.getString(22));
            map.put("OverheadTransmission", cursor.getString(23));
            map.put("LegalDisputeId", cursor.getString(24));
            map.put("SourceWaterId", cursor.getString(25));
            map.put("ElectricitySourceId", cursor.getString(26));
            map.put("DripperSpacing", cursor.getString(27));
            map.put("DischargeRate", cursor.getString(28));
            map.put("CreateBy", cursor.getString(29));
            map.put("AndroidDate", cursor.getString(30));
            map.put("Latitude", cursor.getString(32));
            map.put("Longitude", cursor.getString(31));
            map.put("Accuracy", cursor.getString(33));
            map.put("Street1", cursor.getString(34));
            map.put("Street2", cursor.getString(35));
            map.put("StateId", cursor.getString(36));
            map.put("DistrictId", cursor.getString(37));
            map.put("BlockId", cursor.getString(38));
            map.put("PanchayatId", cursor.getString(39));
            map.put("VillageId", cursor.getString(40));
            map.put("PinCodeId", cursor.getString(41));
            map.put("AddressType", cursor.getString(42));
            map.put("OwnerName", cursor.getString(43));
            map.put("OwnerMobile", cursor.getString(44));
            newFarmBlockUniqueId = cursor.getString(0);
            String strLandCharacteristic = "";
            selectQuery = "SELECT LandCharacteristicId FROM FarmBlockLandCharacteristic WHERE FarmBlockUniqueId ='" + newFarmBlockUniqueId + "' ";
            cursorLandCharacteristic = db.rawQuery(selectQuery, null);
            while (cursorLandCharacteristic.moveToNext()) {
                mapcharacteristic = new HashMap<>();
                strLandCharacteristic = strLandCharacteristic + cursorLandCharacteristic.getString(0) + "^";
                wordCharacteristic.add(mapcharacteristic);
            }
            cursorLandCharacteristic.close();

            String strLandIssue = "";
            selectQuery = "SELECT LandIssueId FROM FarmBlockLandIssue WHERE FarmBlockUniqueId ='" + newFarmBlockUniqueId + "' ";
            cursorLandIssue = db.rawQuery(selectQuery, null);
            while (cursorLandIssue.moveToNext()) {
                mapissue = new HashMap<>();
                strLandIssue = strLandIssue + cursorLandIssue.getString(0) + "^";
                wordIssue.add(mapissue);
            }
            cursorLandIssue.close();
            map.put("LandCharacteristic", strLandCharacteristic);
            map.put("LandIssue", strLandIssue);
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all New Cropping Pattern For Sync">
    public ArrayList<HashMap<String, String>> getNewCroppingPattern() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT fcp.FarmBlockUniqueId, fcp.CropVarietyId,fcp.Acreage,fcp.SeasonId FROM FarmBlockCroppingPattern fcp, FarmBlock fb WHERE fcp.FarmBlockUniqueId = fb.FarmBlockUniqueId AND fb.FarmBlockCode IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmBlockUniqueId", cursor.getString(0));
            map.put("CropVarietyId", cursor.getString(1));
            map.put("Acreage", cursor.getString(2));
            map.put("SeasonId", cursor.getString(3));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Get the list of Pending Dispatches for Delivery">
    public ArrayList<HashMap<String, String>> getPendingDispatchesForDelivery() {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
        /*selectQuery = "SELECT t1.Id, t1.Code, t1.VehicleNo, t1.DispatchForName, t1.DispatchForMobile, SUM(t2.Quantity) AS TotalDispatch,  " +
                "t1.DriverName, t1.DriverMobileNo  " +
                "FROM PendingDispatchForDelivery t1, PendingDispatchDetailsForDelivery t2, PaymentAgainstDispatchDelivery t3 " +
                "WHERE t2.DispatchId = t1.Id AND t3.DispatchId <> t1.Id " +
                "GROUP BY t1.Id,t1.Code, t1.VehicleNo, t1.DispatchForName, t1.DispatchForMobile, t1.DriverName, t1.DriverMobileNo ORDER BY t1.Id";*/
        /*selectQuery = "SELECT Id, Code, VehicleNo, DispatchForName, 10 AS TotalDispatch FROM " +
                "PendingDispatchForDelivery";*/
        selectQuery = "SELECT t1.Id, t1.Code, t1.VehicleNo, t1.DispatchForId, t1.DispatchForName, t1.DispatchForMobile, SUM(t2.Quantity) AS TotalDispatch, t1.DriverName, t1.DriverMobileNo   " +
                "FROM PendingDispatchForDelivery t1, PendingDispatchDetailsForDelivery t2 " +
                "WHERE t2.DispatchId = t1.Id AND t1.Id NOT IN (SELECT DISTINCT DispatchId FROM PaymentAgainstDispatchDelivery) " +
                "GROUP BY t1.Id,t1.Code, t1.VehicleNo, t1.DispatchForId, t1.DispatchForName, t1.DispatchForMobile, t1.DriverName, t1.DriverMobileNo ORDER BY t1.Id";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            map.put("Id", cursor.getString(0));
            map.put("Code", cursor.getString(1));
            map.put("VehicleNo", cursor.getString(2));
            map.put("DispatchForId", cursor.getString(3));
            map.put("DispatchForName", cursor.getString(4));
            map.put("DispatchForMobile", cursor.getString(5));
            map.put("TotalDispatch", cursor.getString(6));
            map.put("DriverName", cursor.getString(7));
            map.put("DriverMobileNo", cursor.getString(8));
            dataList.add(map);
        }
        cursor.close();
        return dataList;
    }
    //</editor-fold>

    //<editor-fold desc="Get the list of Dispatch Items for selected Delivery">
    public ArrayList<HashMap<String, String>> getPendingDispatchItemsForDelivery(String dispatchId) {
        ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
        selectQuery = "SELECT t1.DispatchId, t1.BookingId, t1.Rate, t1.PolybagTypeId, t1.PolybagTitle, t1.Quantity, ifnull(t2. Quantity, 0) AS DeliveryQuantity " +
                "FROM PendingDispatchDetailsForDelivery t1 " +
                "LEFT OUTER JOIN DeliveryDetailsForDispatch t2 " +
                "ON t1.DispatchId = t2.DispatchId AND t1.PolybagTypeId = t2.DispatchItemId " +
                "WHERE t1.DispatchId = '" + dispatchId + "'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            map.put("DispatchId", cursor.getString(0));
            map.put("BookingId", cursor.getString(1));
            map.put("Rate", cursor.getString(2));
            map.put("PolybagId", cursor.getString(3));
            map.put("PolybagTitle", cursor.getString(4));
            map.put("Quantity", cursor.getString(5));
            map.put("DeliveryQuantity", cursor.getString(6));
            dataList.add(map);
        }
        cursor.close();
        return dataList;
    }
    //</editor-fold>

    //<editor-fold desc="Get Balance for Farmer / Nursery">
    public int getBalanceForFarmerNursery(String farmerNurseryId) {
        int balance = 0;
        selectQuery = "SELECT BalanceAmount FROM BalanceDetailsForFarmerNursery WHERE FarmerNurseryId = '"+ farmerNurseryId +"'";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                balance = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return balance;
    }
    //</editor-fold>

    //<editor-fold desc="Get Balance for Farmer / Nursery">
    public String getShortCloseReason(String dispatchId) {
        String reason = "";
        selectQuery = "SELECT ifnull(t2.Title, '') AS SCReason " +
                "FROM PendingDispatchForDelivery t1 " +
                "LEFT OUTER JOIN ShortCloseReason t2 ON t1.ShortCloseReasonId = t2.Id " +
                "WHERE t1.Id = '"+ dispatchId +"'";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                /*reason = cursor.getInt(1);*/
                reason = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        return reason;
    }
    //</editor-fold>



    //<editor-fold desc="To get all New Coordinates For Sync">
    public ArrayList<HashMap<String, String>> getNewCoordinates() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT fc.FarmBlockUniqueId, fc.Latitude,fc.Longitude,fc.Accuracy, fc.Id FROM FarmBlockCoordinates fc, FarmBlock fb WHERE fc.FarmBlockUniqueId = fb.FarmBlockUniqueId AND fb.FarmBlockCode IS NULL ORDER BY fc.FarmBlockUniqueId, fc.Id ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmBlockUniqueId", cursor.getString(0));
            map.put("Latitude", cursor.getString(1));
            map.put("Longitude", cursor.getString(2));
            map.put("Accuracy", cursor.getString(3));
            map.put("OrderId", cursor.getString(4));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Land Characteristic Details by Farm Block Unique Id">
    public List<LandCharacteristic> getLandCharacteristicCheckedByFarmBlockUniqueId(String farmBlockUniqueId) {
        List<LandCharacteristic> labels = new ArrayList<LandCharacteristic>();
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT DISTINCT lc.Id, lc.Title, ifnull(flc.LandCharacteristicId,0) FROM LandCharacteristic lc, FarmBlockLandCharacteristic flc WHERE lc.Id = flc.LandCharacteristicId AND flc.FarmBlockUniqueId ='" + farmBlockUniqueId + "' ORDER BY lc.Title  COLLATE NOCASE ASC ";
        else
            selectQuery = "SELECT DISTINCT lc.Id, lc.TitleHindi, ifnull(flc.LandCharacteristicId,0) FROM LandCharacteristic lc, FarmBlockLandCharacteristic flc WHERE lc.Id = flc.LandCharacteristicId AND flc.FarmBlockUniqueId ='" + farmBlockUniqueId + "' ORDER BY lc.Title  COLLATE NOCASE ASC ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new LandCharacteristic(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Land Issue Details by Farm Block Unique Id">
    public List<LandCharacteristic> getLandIssueCheckedByFarmBlockUniqueId(String farmBlockUniqueId) {
        List<LandCharacteristic> labels = new ArrayList<LandCharacteristic>();
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT DISTINCT li.Id, li.Title, ifnull(fli.LandIssueId,0) FROM LandIssue li, FarmBlockLandIssue fli WHERE li.Id = fli.LandIssueId AND fli.FarmBlockUniqueId ='" + farmBlockUniqueId + "' ORDER BY li.Title  COLLATE NOCASE ASC ";
        else
            selectQuery = "SELECT DISTINCT li.Id, li.TitleHindi, ifnull(fli.LandIssueId,0) FROM LandIssue li, FarmBlockLandIssue fli WHERE li.Id = fli.LandIssueId AND fli.FarmBlockUniqueId ='" + farmBlockUniqueId + "' ORDER BY li.Title  COLLATE NOCASE ASC ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new LandCharacteristic(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get Coordinates from FarmBlockCoordinates Table">
    public List<CustomCoord> GetFarmBlockCoordinates(String farmBlockUniqueId) {
        List<CustomCoord> gps = new ArrayList<CustomCoord>();

        selectQuery = "SELECT DISTINCT Id, Latitude, Longitude,Accuracy FROM FarmBlockCoordinates WHERE FarmBlockUniqueId =  '" + farmBlockUniqueId + "' ORDER BY Id";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            gps.add(new CustomCoord(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }
        cursor.close();
        return gps;

    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Farmer other details for Display">
    public List<FarmerOtherData> getFarmerOtherData(String farmerUniqueId) {
        List<FarmerOtherData> labels = new ArrayList<FarmerOtherData>();
        selectQuery = "SELECT SoilTypeId, IrrigationSystemId, RiverId, DamId, WaterSourceId,  ElectricitySourceId FROM FarmerOtherDetails WHERE FarmerUniqueId ='" + farmerUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new FarmerOtherData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Method to get count of farm block by farmer">
    public int getFarmBlockCountByFarmerId(String farmerId) {
            int id = 0;
            selectQuery = "SELECT COUNT(Id) FROM FarmBlock WHERE FarmerId = '" + farmerId + "' ";
            cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            } while (cursor.moveToNext());
        }
        return id;
    }
    //</editor-fold>

    //<editor-fold desc="To get all New Farm Blocks For Sync">
    public ArrayList<HashMap<String, String>> getFarmBlocksWithCode() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> wordCharacteristic = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> wordIssue = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT fb.FarmBlockUniqueId,fb.KhataNo,fb.KhasraNo,fb.Acerage,ifnull(fb.SoilTypeId,0),ifnull((CASE WHEN fb.ElevationMSL='' THEN 0 ELSE fb.ElevationMSL END),0),ifnull((CASE WHEN fb.PHChemical='' THEN 0 ELSE fb.PHChemical END) ,0),ifnull((CASE WHEN fb.Nitrogen='' THEN 0 ELSE fb.Nitrogen END),0),ifnull((CASE WHEN fb.Potash='' THEN 0 ELSE fb.Potash END),0) ,ifnull((CASE WHEN fb.Phosphorus='' THEN 0 ELSE fb.Phosphorus END),0),ifnull((CASE WHEN fb.OrganicCarbonPerc='' THEN 0 ELSE fb.OrganicCarbonPerc END),0),ifnull((CASE WHEN fb.Magnesium='' THEN 0 ELSE fb.Magnesium END),0),ifnull((CASE WHEN fb.Calcium='' THEN 0 ELSE fb.Calcium END),0),ifnull(fb.ExistingUseId,0),ifnull(fb.CommunityUseId,0) ,ifnull(fb.ExistingHazardId,0),ifnull(fb.RiverId,0),ifnull(fb.DamId,0),ifnull(fb.IrrigationId,0),fb.OverheadTransmission,ifnull(fb.LegalDisputeId,0) ,ifnull(fb.SourceWaterId,0),ifnull(fb.ElectricitySourceId,0),ifnull((CASE WHEN fb.DripperSpacing='' THEN 0 ELSE fb.DripperSpacing END),0),ifnull((CASE WHEN fb.DischargeRate='' THEN 0 ELSE fb.DischargeRate END),0),fb.CreateBy,fb.CreateDate,fb.Longitude,fb.Latitude,fb.Accuracy FROM FarmBlock fb WHERE fb.FarmBlockCode IS NOT NULL AND IsSync =''";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            String newFarmBlockUniqueId = "";
            map.put("FarmBlockUniqueId", cursor.getString(0));
            map.put("KhataNo", cursor.getString(1));
            map.put("KhasraNo", cursor.getString(2));
            map.put("Acerage", cursor.getString(3));
            map.put("SoilTypeId", cursor.getString(4));
            map.put("ElevationMSL", cursor.getString(5));
            map.put("SoliPH", cursor.getString(6));
            map.put("Nitrogen", cursor.getString(7));
            map.put("Potash", cursor.getString(8));
            map.put("Phosphorus", cursor.getString(9));
            map.put("OrganicCarbonPerc", cursor.getString(10));
            map.put("Magnesium", cursor.getString(11));
            map.put("Calcium", cursor.getString(12));
            map.put("ExistingUseId", cursor.getString(13));
            map.put("CommunityUseId", cursor.getString(14));
            map.put("ExistingHazardId", cursor.getString(15));
            map.put("RiverId", cursor.getString(16));
            map.put("DamId", cursor.getString(17));
            map.put("IrrigationId", cursor.getString(18));
            map.put("OverheadTransmission", cursor.getString(19));
            map.put("LegalDisputeId", cursor.getString(20));
            map.put("SourceWaterId", cursor.getString(21));
            map.put("ElectricitySourceId", cursor.getString(22));
            map.put("DripperSpacing", cursor.getString(23));
            map.put("DischargeRate", cursor.getString(24));
            map.put("CreateBy", cursor.getString(25));
            map.put("AndroidDate", cursor.getString(26));
            map.put("Latitude", cursor.getString(27));
            map.put("Longitude", cursor.getString(28));
            map.put("Accuracy", cursor.getString(29));

            newFarmBlockUniqueId = cursor.getString(0);
            String strLandCharacteristic = "";
            selectQuery = "SELECT ch.LandCharacteristicId FROM FarmBlockLandCharacteristic ch, FarmBlock fb WHERE ch.FarmBlockUniqueId=fb.FarmBlockUniqueId AND fb.FarmBlockCode IS NOT NULL AND ch.FarmBlockUniqueId='" + newFarmBlockUniqueId + "' ";
            cursor = db.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                mapcharacteristic = new HashMap<>();
                strLandCharacteristic = strLandCharacteristic + cursor.getString(0) + "^";
                wordCharacteristic.add(mapcharacteristic);
            }
            cursor.close();

            String strLandIssue = "";
            selectQuery = "SELECT li.LandIssueId FROM FarmBlockLandIssue li , FarmBlock fb WHERE li.FarmBlockUniqueId=fb.FarmBlockUniqueId AND fb.FarmBlockCode IS NOT NULL AND li.FarmBlockUniqueId ='" + newFarmBlockUniqueId + "' ";
            cursor = db.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                mapissue = new HashMap<>();
                strLandIssue = strLandIssue + cursor.getString(0) + "^";
                wordIssue.add(mapissue);
            }
            cursor.close();
            map.put("LandCharacteristic", strLandCharacteristic);
            map.put("LandIssue", strLandIssue);
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Cropping Pattern for Farm Block For Sync">
    public ArrayList<HashMap<String, String>> getFarmBlockCroppingPattern() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT fcp.FarmBlockUniqueId, fcp.CropVarietyId,fcp.Acreage,fcp.SeasonId FROM FarmBlockCroppingPattern fcp, FarmBlock fb WHERE fcp.FarmBlockUniqueId = fb.FarmBlockUniqueId AND fb.FarmBlockCode IS NOT NULL AND fb.IsSync='' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmBlockUniqueId", cursor.getString(0));
            map.put("CropVarietyId", cursor.getString(1));
            map.put("Acreage", cursor.getString(2));
            map.put("SeasonId", cursor.getString(3));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Farm Block Coordinates For Sync">
    public ArrayList<HashMap<String, String>> getFarmBlockCoordinates() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT fc.FarmBlockUniqueId, fc.Latitude,fc.Longitude,fc.Accuracy,fc.Id FROM FarmBlockCoordinates fc, FarmBlock fb WHERE fc.FarmBlockUniqueId = fb.FarmBlockUniqueId AND fb.FarmBlockCode IS NOT NULL AND fb.IsSync=''";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmBlockUniqueId", cursor.getString(0));
            map.put("Latitude", cursor.getString(1));
            map.put("Longitude", cursor.getString(2));
            map.put("Accuracy", cursor.getString(3));
            map.put("OrderId", cursor.getString(4));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Plantation for Display">
    public List<PlantationData> getPlantationList(String farmBlockUniqueId) {
        List<PlantationData> labels = new ArrayList<PlantationData>();

        if (userlang.equalsIgnoreCase("en")) {
            selectQuery = "SELECT DISTINCT " +
                    "pl.PlantationUniqueId, " +
                    "pl.FarmerUniqueId, " +
                    "'FarmerName' AS FarmerName, " +
                    "pl.FarmBlockUniqueId, " +
                    "pl.ZoneId, " +
                    "'Zone' AS Zone, " +
                    "pl.CropId, " +
                    "cr.Title AS Crop, " +
                    "pl.CropVarietyId, " +
                    "cvar.Title AS CropVariety, " +
                    "pt.Id AS PlantTypeId, " +
                    "pt.Title AS PlantType, " +
                    "ma.Id AS MonthAgeId, " +
                    "ma.Title AS MonthAge, " +
                    "pl.Acreage, " +
                    "pl.PlantationDate, " +
                    "ps.Id AS PlantingSystemId, " +
                    "ps.Title AS PlantingSystem, " +
                    "pl.PlantRow, " +
                    "pl.PlantColumn, " +
                    "pl.Balance, " +
                    "pl.TotalPlant, " +
                    "pl.PlantationCode " +
                    "FROM FarmerPlantation pl, PlantType pt, MonthAge ma, PlantingSystem ps, Crop cr, Variety cvar  " +
                    "WHERE " +
                    "pt.Id = pl.PlantTypeId " +
                    "AND  ma.Id = pl.MonthAgeId " +
                    "AND ps.Id = pl.PlantingSystemId " +
                    "AND cr.Id = pl.CropId " +
                    "AND cvar.Id = pl.CropVarietyId AND pl.FarmBlockUniqueId= '" + farmBlockUniqueId + "' ";
        } else {
            selectQuery = "SELECT DISTINCT " +
                    "pl.PlantationUniqueId, " +
                    "pl.FarmerUniqueId, " +
                    "'FarmerName' AS FarmerName, " +
                    "pl.FarmBlockUniqueId, " +
                    "pl.ZoneId, " +
                    "'Zone' AS Zone, " +
                    "pl.CropId, " +
                    "cr.TitleHindi AS Crop, " +
                    "pl.CropVarietyId, " +
                    "cvar.TitleHindi AS CropVariety, " +
                    "pt.Id AS PlantTypeId, " +
                    "pt.Title AS PlantType, " +
                    "ma.Id AS MonthAgeId, " +
                    "ma.Title AS MonthAge, " +
                    "pl.Acreage, " +
                    "pl.PlantationDate, " +
                    "ps.Id AS PlantingSystemId, " +
                    "ps.Title AS PlantingSystem, " +
                    "pl.PlantRow, " +
                    "pl.PlantColumn, " +
                    "pl.Balance, " +
                    "pl.TotalPlant, " +
                    "pl.PlantationCode " +
                    "FROM FarmerPlantation pl, PlantType pt, MonthAge ma, PlantingSystem ps, Crop cr, Variety cvar  " +
                    "WHERE " +
                    "pt.Id = pl.PlantTypeId " +
                    "AND  ma.Id = pl.MonthAgeId " +
                    "AND ps.Id = pl.PlantingSystemId " +
                    "AND cr.Id = pl.CropId " +
                    "AND cvar.Id = pl.CropVarietyId AND pl.FarmBlockUniqueId= '" + farmBlockUniqueId + "' ";
        }

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new PlantationData(
                    cursor.getString(0), cursor.getString(22),
                    cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7),
                    cursor.getString(8), cursor.getString(9),
                    cursor.getString(10), cursor.getString(11),
                    cursor.getString(12), cursor.getString(13),
                    cursor.getString(14), cursor.getString(15),
                    cursor.getString(16), cursor.getString(17),
                    cursor.getString(18), cursor.getString(19),
                    cursor.getString(20), cursor.getString(21)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get Plantation details by unique id from Main table">
    public ArrayList<String> getPlantationDetailByUniqueId(String plantationUniqueId) {
        ArrayList<String> plantationdetails = new ArrayList<String>();
        String newFileName = "";
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT DISTINCT pl.PlantationUniqueId,pl.ZoneId,cr.Title AS Crop,cvar.Title AS CropVariety,pt.Title AS PlantType,ma.Title AS MonthAge,pl.Acreage,pl.PlantationDate,ps.Id AS PlantingSystemId,ps.Title AS PlantingSystem,pl.PlantRow,pl.PlantColumn,pl.Balance,pl.TotalPlant, ifnull(pl.PlantationCode,'') FROM FarmerPlantation pl, PlantType pt, MonthAge ma, PlantingSystem ps, Crop cr, Variety cvar  WHERE pt.Id = pl.PlantTypeId AND  ma.Id = pl.MonthAgeId AND ps.Id = pl.PlantingSystemId AND cr.Id = pl.CropId AND cvar.Id = pl.CropVarietyId AND pl.PlantationUniqueId='" + plantationUniqueId + "' ";
        else
            selectQuery = "SELECT DISTINCT pl.PlantationUniqueId,pl.ZoneId,cr.TitleHindi AS Crop,cvar.TitleHindi AS CropVariety,pt.TitleHindi AS PlantType,ma.Title AS MonthAge,pl.Acreage,pl.PlantationDate,ps.Id AS PlantingSystemId,ps.Title AS PlantingSystem,pl.PlantRow,pl.PlantColumn,pl.Balance,pl.TotalPlant, ifnull(pl.PlantationCode,'') FROM FarmerPlantation pl, PlantType pt, MonthAge ma, PlantingSystem ps, Crop cr, Variety cvar  WHERE pt.Id = pl.PlantTypeId AND  ma.Id = pl.MonthAgeId AND ps.Id = pl.PlantingSystemId AND cr.Id = pl.CropId AND cvar.Id = pl.CropVarietyId AND pl.PlantationUniqueId='" + plantationUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            plantationdetails.add(cursor.getString(0));
            plantationdetails.add(cursor.getString(1));
            plantationdetails.add(cursor.getString(2));
            plantationdetails.add(cursor.getString(3));
            plantationdetails.add(cursor.getString(4));
            plantationdetails.add(cursor.getString(5));
            plantationdetails.add(cursor.getString(6));
            plantationdetails.add(cursor.getString(7));
            plantationdetails.add(cursor.getString(8));
            plantationdetails.add(cursor.getString(9));
            plantationdetails.add(cursor.getString(10));
            plantationdetails.add(cursor.getString(11));
            plantationdetails.add(cursor.getString(12));
            plantationdetails.add(cursor.getString(13));
            plantationdetails.add(cursor.getString(14));
        }
        cursor.close();

        return plantationdetails;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get Nursery Plantation details by unique id from Main table">
    // TODO Add plantation code in the NurseryPlantation
    public ArrayList<String> getNurseryPlantationDetailByUniqueId(String plantationUniqueId) {
        ArrayList<String> list = new ArrayList<>();
        selectQuery = "SELECT DISTINCT " +
                "pl.PlantationUniqueId, " +
                "pl.PlantationCode, " +
                "pl.NurseryUniqueId, " +
                "pl.NurseryId, " +
                "zn.UniqueId AS ZoneUniqueId, " +
                "pl.ZoneId, " +
                "zn.Title AS Zone,  " +
                "cr.Id AS CropId, " +
                "cr.Title AS Crop, " +
                "cvar.Id AS CropVarieryId, " +
                "cvar.Title AS CropVariety," +
                "pt.Id AS plantTypeId, " +
                "pt.Title AS PlantType, " +
                "ma.Id AS MonthAgeId, " +
                "ma.Title AS MonthAge," +
                "pl.Acreage, " +
                "pl.PlantationDate, " +
                "ps.Id AS PlantingSystemId," +
                "ps.Title AS PlantingSystem, " +
                "pl.PlantRow, " +
                "pl.PlantColumn," +
                "pl.Balance, " +
                "pl.TotalPlant " +
                "FROM " +
                "NurseryPlantation pl, PlantType pt, MonthAge ma, PlantingSystem ps, Crop cr, Variety cvar , NurseryZone zn " +
                "WHERE pt.Id = pl.PlantTypeId " +
                "AND  ma.Id = pl.MonthAgeId " +
                "AND ps.Id = pl.PlantingSystemId " +
                "AND cr.Id = pl.CropId " +
                "AND cvar.Id = pl.CropVarietyId " +
                "AND zn.Id = pl.ZoneId " +
                "AND pl.PlantationUniqueId='" + plantationUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
            list.add(cursor.getString(1));
            list.add(cursor.getString(2));
            list.add(cursor.getString(3));
            list.add(cursor.getString(4));
            list.add(cursor.getString(5));
            list.add(cursor.getString(6));
            list.add(cursor.getString(7));
            list.add(cursor.getString(8));
            list.add(cursor.getString(9));
            list.add(cursor.getString(10));
            list.add(cursor.getString(11));
            list.add(cursor.getString(12));
            list.add(cursor.getString(13));
            list.add(cursor.getString(14));
            list.add(cursor.getString(15));
            list.add(cursor.getString(16));
            list.add(cursor.getString(17));
            list.add(cursor.getString(18));
            list.add(cursor.getString(19));
            list.add(cursor.getString(20));
            list.add(cursor.getString(21));
            list.add(cursor.getString(22));
        }
        cursor.close();

        return list;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Nursery Plantation For Sync">
    public ArrayList<HashMap<String, String>> getNurseryPlantationForSync() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<>();
        selectQuery = "SELECT DISTINCT " +
                "np.PlantationUniqueId, nu.UniqueId, np.NurseryId, np.ZoneId, np.CropVarietyId, np.MonthAgeId, np.Acreage, np.PlantationDate, " +
                "np.PlantTypeId, np.PlantingSystemId, np.PlantRow, np.PlantColumn, np.Balance, np.TotalPlant, " +
                "np.CreateBy, np.CreateDate, np.Longitude, np.Latitude, np.Accuracy " +
                "FROM NurseryPlantation np, Nursery nu WHERE np.NurseryId = nu.Id " +
                " AND np.IsSync IS NULL OR np.IsSync ='' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            map.put("PlantationUniqueId", cursor.getString(0));
            map.put("NurseryUniqueId", cursor.getString(1));
            map.put("NurseryId", cursor.getString(2));
            map.put("ZoneId", cursor.getString(3));
            map.put("CropVarietyId", cursor.getString(4));
            map.put("MonthAgeId", cursor.getString(5));
            map.put("Acreage", cursor.getString(6));
            map.put("PlantationDate", cursor.getString(7));
            map.put("PlantTypeId", cursor.getString(8));
            map.put("PlantingSystemId", cursor.getString(9));
            map.put("PlantRow", cursor.getString(10));
            map.put("PlantColumn", cursor.getString(11));
            map.put("Balance", cursor.getString(12));
            map.put("TotalPlant", cursor.getString(13));
            map.put("CreateBy", cursor.getString(14));
            map.put("CreateDate", cursor.getString(15));
            map.put("Longitude", cursor.getString(16));
            map.put("Latitude", cursor.getString(17));
            map.put("Accuracy", cursor.getString(18));

            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check Is Log Out Allowed for User">
    public boolean IslogoutAllowed() {
        boolean isRequired = true;
        int countFarmer, countFarmerDocuments, countFarmerProof, countFarmBlock, countPlantation, countInterCrop, countJobCard, countNuseryPlant, countNurseryInterCrop, countVisit, countjcPending, countrcPending, countjcdocCount, countrcdocPending, countVisitPhoto;

        selectQuery = "SELECT Id FROM Farmer WHERE IsSync != '1' OR IsSync IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        countFarmer = cursor.getCount();
        cursor.close();

        selectQuery = "SELECT Id FROM FarmerDocuments WHERE IsSync != '1' OR IsSync IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        countFarmerDocuments = cursor.getCount();
        cursor.close();

        selectQuery = "SELECT Id FROM FarmerProof WHERE IsSync != '1' OR IsSync IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        countFarmerProof = cursor.getCount();
        cursor.close();

        selectQuery = "SELECT Id FROM FarmBlock WHERE IsSync != '1' OR IsSync IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        countFarmBlock = cursor.getCount();
        cursor.close();

        selectQuery = "SELECT Id FROM FarmerPlantation WHERE IsSync != '1' OR IsSync IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        countPlantation = cursor.getCount();
        cursor.close();


        selectQuery = "SELECT Id FROM InterCropping WHERE IsSync != '1' OR IsSync IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        countInterCrop = cursor.getCount();
        cursor.close();

        selectQuery = "SELECT Id FROM JobCard WHERE IsSync != '1' OR IsSync IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        countJobCard = cursor.getCount();
        cursor.close();

        selectQuery = "SELECT Id FROM NurseryPlantation WHERE IsSync != '1' OR IsSync IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        countNuseryPlant = cursor.getCount();
        cursor.close();

        selectQuery = "SELECT Id FROM NurseryInterCropping WHERE IsSync != '1' OR IsSync IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        countNurseryInterCrop = cursor.getCount();
        cursor.close();

        selectQuery = "SELECT Id FROM VisitReport WHERE (IsSync != '1' OR IsSync IS NULL) AND IsTemp='0' ";
        cursor = db.rawQuery(selectQuery, null);
        countVisit = cursor.getCount();
        cursor.close();

        selectQuery = "SELECT Id FROM VisitReportPhoto WHERE (IsSync != '1' OR IsSync IS NULL) AND IsTemp='0' AND FileName !='' ";
        cursor = db.rawQuery(selectQuery, null);
        countVisitPhoto = cursor.getCount();
        cursor.close();

        selectQuery = "SELECT Id FROM JobCardPending WHERE IsSync = '0'";
        cursor = db.rawQuery(selectQuery, null);
        countjcPending = cursor.getCount();
        cursor.close();

        selectQuery = "SELECT Id FROM Recommendation WHERE (IsSyncData != '1' OR IsSyncData IS NULL) AND IsTemp='0'";
        cursor = db.rawQuery(selectQuery, null);
        countrcPending = cursor.getCount();
        cursor.close();

        selectQuery = "SELECT Id FROM RecommendationDetail WHERE (IsSyncFile != '1' OR IsSyncFile IS NULL) AND IsTemp='0'  AND FileName !='' ";
        cursor = db.rawQuery(selectQuery, null);
        countrcdocPending = cursor.getCount();
        cursor.close();


        selectQuery = "SELECT Id FROM JobCardDetail WHERE (IsSync != '1' OR IsSync IS NULL) AND FileName !='' ";
        cursor = db.rawQuery(selectQuery, null);
        countjcdocCount = cursor.getCount();
        cursor.close();

        if (countFarmer > 0 || countFarmBlock > 0 || countFarmerProof > 0 || countPlantation > 0 || countInterCrop > 0 || countJobCard > 0 || countNuseryPlant > 0 || countNurseryInterCrop > 0 || countVisit > 0 || countjcPending > 0 || countrcPending > 0 || countjcdocCount > 0 || countrcdocPending > 0 || countVisitPhoto > 0)
            isRequired = false;
        return isRequired;
    }
    //</editor-fold>

    //<editor-fold desc="Method to get Zone Id Count by FarmBlockUniqueId">
    public int getZoneIdByFarmerId(String farmBlockUniqueId) {
        int id = 0;
        selectQuery = "SELECT COUNT(*)+1 FROM FarmerPlantation WHERE FarmBlockUniqueId = '" + farmBlockUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);
            } while (cursor.moveToNext());
        }

        return id;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Inter Cropping">
    public List<FarmBlockCroppingPatternData> getInterCropping() {
        List<FarmBlockCroppingPatternData> labels = new ArrayList<FarmBlockCroppingPatternData>();

        selectQuery = "SELECT  t.Id, t.farmBlockUniqueId, v.CropId, c.Title AS Crop, t.CropVarietyId, v.Title AS CropVariety, t.SeasonId, s.Title AS Season, t.acreage FROM InterCropping t, Crop c, Variety v, Season s WHERE v.CropId = c.Id AND t.CropVarietyId = v.Id AND t.SeasonId = s.Id ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new FarmBlockCroppingPatternData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Inter Cropping by Unique Id">
    public List<InterCroppingData> getInterCroppingByUniqueId(String plantationUniqueId) {
        List<InterCroppingData> labels = new ArrayList<InterCroppingData>();
        if (userlang.equalsIgnoreCase("en"))
            selectQuery = "SELECT t.Id, t.farmBlockUniqueId, v.CropId, c.Title AS Crop, t.CropVarietyId, v.Title AS CropVariety, t.SeasonId, ifnull(t.FinancialYear,'')|| ' '|| s.Title AS Season, t.acreage, CASE WHEN IsSync='1' THEN '1' ELSE '0' END FROM InterCropping t, Crop c, Variety v, Season s WHERE v.CropId = c.Id AND t.CropVarietyId = v.Id AND t.SeasonId = s.Id AND t.FarmerPlantationUniqueId ='" + plantationUniqueId + "' ";
        else
            selectQuery = "SELECT t.Id, t.farmBlockUniqueId, v.CropId, c.TitleHindi AS Crop, t.CropVarietyId, v.TitleHindi AS CropVariety, t.SeasonId, ifnull(t.FinancialYear,'')|| ' '|| s.Title AS Season, t.acreage, CASE WHEN IsSync='1' THEN '1' ELSE '0' END FROM InterCropping t, Crop c, Variety v, Season s WHERE v.CropId = c.Id AND t.CropVarietyId = v.Id AND t.SeasonId = s.Id AND t.FarmerPlantationUniqueId ='" + plantationUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new InterCroppingData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Plantation For Sync">
    public ArrayList<HashMap<String, String>> getPlantationForSync() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT PlantationUniqueId,FarmBlockUniqueId,ZoneId, CropVarietyId,MonthAgeId,Acreage,PlantationDate,PlantTypeId,PlantingSystemId,PlantRow,PlantColumn,Balance,TotalPlant,CreateBy,CreateDate,Longitude,Latitude,Accuracy FROM FarmerPlantation WHERE IsSync IS NULL OR IsSync ='' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("PlantationUniqueId", cursor.getString(0));
            map.put("FarmBlockUniqueId", cursor.getString(1));
            map.put("ZoneId", cursor.getString(2));
            map.put("CropVarietyId", cursor.getString(3));
            map.put("MonthAgeId", cursor.getString(4));
            map.put("Acreage", cursor.getString(5));
            map.put("PlantationDate", cursor.getString(6));
            map.put("PlantTypeId", cursor.getString(7));
            map.put("PlantingSystemId", cursor.getString(8));
            map.put("PlantRow", cursor.getString(9));
            map.put("PlantColumn", cursor.getString(10));
            map.put("Balance", cursor.getString(11));
            map.put("TotalPlant", cursor.getString(12));
            map.put("CreateBy", cursor.getString(13));
            map.put("CreateDate", cursor.getString(14));
            map.put("Longitude", cursor.getString(15));
            map.put("Latitude", cursor.getString(16));
            map.put("Accuracy", cursor.getString(17));

            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Nursery Inter Cropping by Nursery Id">
    public List<NurseryInterCroppingData> getNurseryInterCroppingByPlantationUniqueId(String plantationUniqueId) {
        List<NurseryInterCroppingData> labels = new ArrayList<>();

        selectQuery = "SELECT " +
                "   t.Id, " +
                "   t.InterCroppingUniqueId, " +
                "   t.PlantationUniqueId, " +
                "   t.NurseryId, " +
                "   v.CropId, " +
                "   c.Title AS Crop, " +
                "   t.CropVarietyId, " +
                "   v.Title AS CropVariety, " +
                "   t.SeasonId, " +
                "   IFNULL(t.FinancialYear,'')|| ' '|| s.Title AS Season, " +
                "   t.acreage, " +
                "   CASE WHEN t.IsSync='1' THEN '1' ELSE '0' END " +
                "FROM " +
                "   NurseryInterCropping t, " +
                "   Crop c, " +
                "   Variety v, " +
                "   Season s " +
                "WHERE " +
                "   v.CropId = c.Id " +
                "   AND t.CropVarietyId = v.Id " +
                "   AND t.SeasonId = s.Id " +
                "   AND t.PlantationUniqueId ='" + plantationUniqueId + "' ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            NurseryInterCroppingData data = new NurseryInterCroppingData();
            data.setId(cursor.getString(0));
            data.setInterCroppingUniqueId(cursor.getString(1));
            data.setPlantationUniqueId(cursor.getString(2));
            data.setNurseryId(cursor.getString(3));
            data.setCropId(cursor.getString(4));
            data.setCrop(cursor.getString(5));
            data.setCropVarietyId(cursor.getString(6));
            data.setCropVariety(cursor.getString(7));
            data.setSeasonId(cursor.getString(8));
            data.setSeason(cursor.getString(9));
            data.setAcreage(cursor.getString(10));
            data.setIsSync(cursor.getString(11));
            labels.add(data);
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Intercropping For Sync">
    public ArrayList<HashMap<String, String>> getInterCroppingForSync() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT InterCroppingUniqueId,FarmerPlantationUniqueId,CropVarietyId,Acreage,SeasonId,Longitude,Latitude,Accuracy,CreateBy,CreateDate FROM InterCropping WHERE IsSync IS NULL OR IsSync ='' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("InterCroppingUniqueId", cursor.getString(0));
            map.put("PlantationUniqueId", cursor.getString(1));
            map.put("CropVarietyId", cursor.getString(2));
            map.put("Acreage", cursor.getString(3));
            map.put("SeasonId", cursor.getString(4));
            map.put("Longitude", cursor.getString(5));
            map.put("Latitude", cursor.getString(6));
            map.put("Accuracy", cursor.getString(7));
            map.put("CreateBy", cursor.getString(8));
            map.put("CreateDate", cursor.getString(9));

            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if variety and season already added in inter cropping">
    public Boolean isVarietySeasonInterCroppingAlreadyAdded(String plantationUniqueId, String varietyId, String seasonId) {
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM InterCropping WHERE  FarmerPlantationUniqueId = '" + plantationUniqueId + "' AND CropVarietyId = '" + varietyId + "' AND SeasonId ='" + seasonId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if variety and season already added in Nursery Inter Cropping">
    public Boolean isVarietySeasonNurseryInterCroppingAlreadyAdded(String plantationUniqueId, String varietyId, String seasonId) {
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM NurseryInterCropping " +
                "WHERE  PlantationUniqueId = '" + plantationUniqueId + "' " +
                "AND CropVarietyId = '" + varietyId + "' " +
                "AND SeasonId ='" + seasonId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if variety and season already added in nursery inter cropping">
    public Boolean isVarietySeasonNurseryInterCroppingAlreadyAdded(String nurseryId, String zoneId, String varietyId, String seasonId) {
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM NurseryInterCropping " +
                "WHERE NurseryId = '" + nurseryId + "' " +
                "AND ZoneId = '" + zoneId + "' " +
                "AND CropVarietyId = '" + varietyId + "' AND SeasonId ='" + seasonId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="To get all UniqueId for which Financial Year is not Available">
    public ArrayList<HashMap<String, String>> getInterCropFinUniqueId() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT InterCroppingUniqueId,CreateBy FROM InterCropping WHERE FinancialYear IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("InterCroppingUniqueId", cursor.getString(0));
            map.put("CreateBy", cursor.getString(1));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get Planned Activity By Plantation Unique Id">
    public ArrayList<HashMap<String, String>> getPlannedActivitiesForPlantation(String plantationUniqueId) {
        wordList = new ArrayList<HashMap<String, String>>();

        selectQuery = "SELECT DISTINCT pl.PlannerDetailId,pl.ParameterDetailId,pl.ActivityId, pl.SubActivityId,pl.WeekNO,pl.FarmBlockNurseryUniqueId, fa.Title,ifnull(fs.SubActivityName,''), pl.Quantity, uom.ShortName, pl.Remarks, ifnull(jt.ActivityValue,''),ifnull(jt.UniqueId,''), ifnull(tf.FileName,''), ifnull(jt.PreviousValue,''), ifnull(detsum.TotalValue,'') FROM PlannedActivity pl LEFT OUTER JOIN FarmSubActivity fs ON pl.SubActivityId = fs.FarmSubActivityId LEFT OUTER JOIN JobCardDetailTemp jt ON pl.SubActivityId = jt.FarmSubActivityId AND pl.ActivityId = jt.FarmActivityId AND jt.ActivityType='P'  LEFT OUTER JOIN TempJobCardFile tf ON pl.ActivityId = tf.ActivityId AND pl.SubActivityId = tf.SubActivityId AND Type ='Planned' LEFT OUTER JOIN (SELECT jc.PlantationUniqueId, jc.WeekNo, jcd.FarmActivityId, jcd.FarmSubActivityId, SUM(jcd.ActivityValue) AS TotalValue FROM JobCard jc, JobCardDetail jcd WHERE jc.JobCardUniqueId = jcd.JobCardUniqueId AND jcd.ActivtyType ='P'  AND jcd.ActivityValue !='' GROUP BY jc.PlantationUniqueId,jcd.FarmActivityId, jcd.FarmSubActivityId ) detsum ON pl.ActivityId = detsum.FarmActivityId AND pl.SubActivityId =detsum.FarmSubActivityId AND pl.WeekNO = detsum.WeekNo AND pl.PlantationUniqueId = detsum.PlantationUniqueId, FarmActivity fa,UOM uom  WHERE (CAST((strftime('%s',pl.FromDate)-strftime('%s',DATE('now'))) AS int)/60/60/24) <= 0 AND (CAST((strftime('%s',pl.ToDate)-strftime('%s',DATE('now'))) AS int)/60/60/24)>= 0 AND pl.ActivityId = fa.FarmActivityId AND pl.UOMId = uom.UOMId AND pl.PlantationUniqueId= '" + plantationUniqueId + "' ORDER BY fa.Title  COLLATE NOCASE ASC,fs.SubActivityName COLLATE NOCASE ASC";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("PlannerDetailId", cursor.getString(0));
            map.put("ParameterDetailId", cursor.getString(1));
            map.put("FarmActivityId", cursor.getString(2));
            map.put("FarmSubActivityId", cursor.getString(3));
            map.put("WeekNo", cursor.getString(4));
            map.put("FarmBlockNurseryUniqueId", cursor.getString(5));
            map.put("ActivityName", cursor.getString(6));
            map.put("SubActivityName", cursor.getString(7));
            map.put("Quantity", cursor.getString(8));
            map.put("UOM", cursor.getString(9));
            map.put("Remarks", cursor.getString(10));
            map.put("ActualQty", cursor.getString(11));
            map.put("UniqueId", cursor.getString(12));
            map.put("FileName", cursor.getString(13));
            map.put("PreviousValue", cursor.getString(14));
            map.put("TotalValue", cursor.getString(15));
            wordList.add(map);
        }

        cursor.close();

        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get Recommended Activity By Plantation Unique Id">
    public ArrayList<HashMap<String, String>> getRecommendedActivitiesForPlantation(String plantationUniqueId) {
        wordList = new ArrayList<HashMap<String, String>>();

        //selectQuery = "SELECT DISTINCT 0,0,pl.ActivityId, pl.SubActivityId,pl.WeekNO, pl.FarmBlockNurseryUniqueId,fa.Title,ifnull(fs.SubActivityName,''), pl.Quantity, uom.ShortName, pl.Remarks, ifnull(jt.ActivityValue,''),ifnull(jt.UniqueId,''),ifnull(tf.FileName,''), jt.PreviousValue FROM RecommendedActivity pl LEFT OUTER JOIN FarmSubActivity fs ON pl.SubActivityId = fs.FarmSubActivityId LEFT OUTER JOIN JobCardDetailTemp jt ON pl.SubActivityId = jt.FarmSubActivityId AND pl.ActivityId = jt.FarmActivityId AND jt.ActivityType='R' LEFT OUTER JOIN TempJobCardFile tf ON pl.ActivityId = tf.ActivityId AND pl.SubActivityId = tf.SubActivityId AND Type ='Recommended', FarmActivity fa,UOM uom WHERE (CAST((strftime('%s',pl.FromDate)-strftime('%s',DATE('now'))) AS int)/60/60/24) <= 0 AND (CAST((strftime('%s',pl.ToDate)-strftime('%s',DATE('now'))) AS int)/60/60/24)>= 0 AND pl.ActivityId = fa.FarmActivityId AND pl.UOMId = uom.UOMId AND pl.PlantationUniqueId= '" + plantationUniqueId + "' ORDER BY fa.Title  COLLATE NOCASE ASC,fs.SubActivityName COLLATE NOCASE ASC";
        selectQuery = "SELECT DISTINCT 0,0,pl.ActivityId, pl.SubActivityId,pl.WeekNO, pl.FarmBlockNurseryUniqueId,fa.Title,ifnull(fs.SubActivityName,''), pl.Quantity, uom.ShortName, pl.Remarks, ifnull(jt.ActivityValue,''),ifnull(jt.UniqueId,''),ifnull(tf.FileName,''), ifnull(jt.PreviousValue,''), ifnull(detsum.TotalValue,'') FROM RecommendedActivity pl LEFT OUTER JOIN FarmSubActivity fs ON pl.SubActivityId = fs.FarmSubActivityId LEFT OUTER JOIN JobCardDetailTemp jt ON pl.SubActivityId = jt.FarmSubActivityId AND pl.ActivityId = jt.FarmActivityId AND jt.ActivityType='R' LEFT OUTER JOIN TempJobCardFile tf ON pl.ActivityId = tf.ActivityId AND pl.SubActivityId = tf.SubActivityId AND Type ='Recommended' LEFT OUTER JOIN (SELECT jc.PlantationUniqueId, jc.WeekNo, jcd.FarmActivityId, jcd.FarmSubActivityId, SUM(jcd.ActivityValue) AS TotalValue FROM JobCard jc, JobCardDetail jcd WHERE jc.JobCardUniqueId = jcd.JobCardUniqueId AND jcd.ActivtyType ='R'  AND jcd.ActivityValue !='' GROUP BY jc.PlantationUniqueId,jcd.FarmActivityId, jcd.FarmSubActivityId ) detsum ON pl.ActivityId = detsum.FarmActivityId AND pl.SubActivityId =detsum.FarmSubActivityId AND pl.WeekNO = detsum.WeekNo AND pl.PlantationUniqueId = detsum.PlantationUniqueId, FarmActivity fa,UOM uom WHERE (CAST((strftime('%s',pl.FromDate)-strftime('%s',DATE('now'))) AS int)/60/60/24) <= 0 AND (CAST((strftime('%s',pl.ToDate)-strftime('%s',DATE('now'))) AS int)/60/60/24)>= 0 AND pl.ActivityId = fa.FarmActivityId AND pl.UOMId = uom.UOMId AND pl.PlantationUniqueId= '" + plantationUniqueId + "' ORDER BY fa.Title  COLLATE NOCASE ASC,fs.SubActivityName COLLATE NOCASE ASC";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("PlannerDetailId", cursor.getString(0));
            map.put("ParameterDetailId", cursor.getString(1));
            map.put("FarmActivityId", cursor.getString(2));
            map.put("FarmSubActivityId", cursor.getString(3));
            map.put("WeekNo", cursor.getString(4));
            map.put("FarmBlockNurseryUniqueId", cursor.getString(5));
            map.put("ActivityName", cursor.getString(6));
            map.put("SubActivityName", cursor.getString(7));
            map.put("Quantity", cursor.getString(8));
            map.put("UOM", cursor.getString(9));
            map.put("Remarks", cursor.getString(10));
            map.put("ActualQty", cursor.getString(11));
            map.put("UniqueId", cursor.getString(12));
            map.put("FileName", cursor.getString(13));
            map.put("PreviousValue", cursor.getString(14));
            map.put("TotalValue", cursor.getString(15));
            wordList.add(map);
        }

        cursor.close();

        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get Additional Activity From Temporary Table">
    public ArrayList<HashMap<String, String>> getTempAdditionalActivity() {
        wordList = new ArrayList<HashMap<String, String>>();

        selectQuery = "SELECT DISTINCT fa.Title,ifnull(fs.SubActivityName,''), pl.ActivityValue, ifnull(uom.ShortName, uoms.ShortName), ifnull(pl.UniqueId,''),ifnull(tf.FileName,'') FROM JobCardDetailTemp pl LEFT OUTER JOIN FarmSubActivity fs ON pl.FarmSubActivityId = fs.FarmSubActivityId LEFT OUTER JOIN UOM uoms ON  fs.UOMId = uoms.UOMId LEFT OUTER JOIN TempJobCardFile tf ON pl.FarmActivityId = tf.ActivityId AND pl.FarmSubActivityId = tf.SubActivityId AND Type ='Additional', FarmActivity fa LEFT OUTER JOIN UOM uom ON  fa.UOMId = uom.UOMId  WHERE pl.FarmActivityId = fa.FarmActivityId  AND pl.ActivityType='A' ORDER BY fa.Title  COLLATE NOCASE ASC,fs.SubActivityName COLLATE NOCASE ASC";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("ActivityName", cursor.getString(0));
            map.put("SubActivityName", cursor.getString(1));
            map.put("Quantity", cursor.getString(2));
            map.put("UOM", cursor.getString(3));
            map.put("UniqueId", cursor.getString(4));
            map.put("FileName", cursor.getString(5));
            wordList.add(map);
        }

        cursor.close();

        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get Planned Activity By Plantation Unique Id">
    public ArrayList<HashMap<String, String>> getAdditionalActivitiesForPlantation(String plantationUniqueId) {
        wordList = new ArrayList<HashMap<String, String>>();

        selectQuery = "SELECT act.ActivityId, act.SubActivityId, act.FarmBlockNurseryUniqueId,fa.Title,ifnull(fs.SubActivityName,''),uom.ShortName,act.Remarks from AllActivity act LEFT OUTER JOIN FarmSubActivity fs ON act.SubActivityId = fs.FarmSubActivityId, FarmActivity fa,UOM uom LEFT OUTER JOIN (SELECT ActivityId, SubActivityId, PlantationUniqueId FROM PlannedActivity WHERE (CAST((strftime('%s',FromDate)-strftime('%s',DATE('now'))) AS int)/60/60/24) <= 0 AND (CAST((strftime('%s',ToDate)-strftime('%s',DATE('now'))) AS int)/60/60/24)>= 0 AND PlantationUniqueId='" + plantationUniqueId + "') det ON act.ActicityId = det.ActivityId AND act.SubActivityId = det.SubActivityId AND act.PlantationUniqueId = det.PlantationUniqueId WHERE det.ActivityId IS NULL AND act.ActivityId = fa.FarmActivityId AND act.UOMId = uom.UOMId AND act.PlantationUniqueId= '" + plantationUniqueId + "' ORDER BY fa.Title  COLLATE NOCASE ASC,fs.SubActivityName COLLATE NOCASE ASC";


        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmActivityId", cursor.getString(0));
            map.put("FarmSubActivityId", cursor.getString(1));
            map.put("FarmBlockNurseryUniqueId", cursor.getString(2));
            map.put("ActivityName", cursor.getString(3));
            map.put("SubActivityName", cursor.getString(4));
            map.put("UOM", cursor.getString(5));
            map.put("Remarks", cursor.getString(6));
            wordList.add(map);
        }

        cursor.close();

        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get remarks by activity , sub activity and Plantation unique id">
    public String getRemarksByActSubActId(String plantationUniqueId, String activityId, String subactivityId) {
        String remarks = "";
        selectQuery = "SELECT Remarks FROM AllActivity WHERE ActivityId = '" + activityId + "' AND SubActivityId = '" + subactivityId + "' AND PlantationUniqueId = '" + plantationUniqueId + "' ";

        cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                remarks = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return remarks;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Observation for Display">
    public List<ObservationData> getObservationList(String visitUniqueId) {
        List<ObservationData> labels = new ArrayList<ObservationData>();

        selectQuery = "SELECT DISTINCT rd.Id, ph.DefectId, de.Title, rd.Remarks, CASE WHEN ph.Id IS NULL THEN '0' ELSE '1' END AS IsAttachment, CASE WHEN vr.IsSync = 1 THEN '1' ELSE '0' END AS IsSync FROM VisitReport vr, VisitReportDetail rd LEFT OUTER JOIN VisitReportPhoto ph ON rd.VisitUniqueId = ph.VisitUniqueId AND rd.DefectId = ph.DefectId, Defect de WHERE vr.VisitUniqueId = rd.VisitUniqueId AND rd.DefectId = de.Id AND vr.VisitUniqueId='" + visitUniqueId + "' ORDER BY LOWER(de.Title), LOWER(de.Title)";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new ObservationData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get detail of Routine Visit for Display">
    public List<VisitViewData> GetVisitViewDetail(String visitUniqueId) {
        List<VisitViewData> labels = new ArrayList<VisitViewData>();
        selectQuery = "SELECT DISTINCT vr.FarmerUniqueId, vr.FarmBlockNurseryUniqueId, vr.PlantationId, fb.FarmBlockCode, fa.Salutation||' '||fa.FirstName||(CASE WHEN fa.MiddleName='' THEN '' ELSE ' '|| fa.MiddleName END)||' '|| fa.LastName AS Farmer, fa.Mobile, fa.FarmerCode, va.Title ||' - '||REPLACE(pl.PlantationDate,'/','-') AS Plantation, vr.PlantHeight, vr.PlantStatusId, ps.Title AS PlantStatus, vr.Days FROM VisitReport vr LEFT OUTER JOIN Farmer fa ON vr.FarmerUniqueId = fa.FarmerUniqueId LEFT OUTER JOIN FarmBlock fb ON vr.FarmBlockNurseryUniqueId= fb.FarmBlockUniqueId LEFT OUTER JOIN FarmerPlantation pl ON vr.PlantationId = pl.PlantationUniqueId LEFT OUTER JOIN Variety va ON va.Id = pl.CropVarietyId LEFT OUTER JOIN PlantStatus ps ON vr.PlantStatusId = ps.Id WHERE FarmBlockNurseryType = 'FarmBlock' AND VisitUniqueId = '" + visitUniqueId + "';";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new VisitViewData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of plantation by zone id">
    public List<CustomData> getPlantationListByZoneId(String zoneId) {
        List<CustomData> labels = new ArrayList<CustomData>();
        selectQuery = "SELECT DISTINCT pl.PlantationUniqueId, (var.Title||' - '|| pl.PlantationDate) AS Name FROM NurseryPlantation pl, Crop cr, Variety var, NurseryZone zn WHERE pl.CropId = cr.Id AND pl.CropVarietyId = var.Id AND zn.Id = pl.ZoneId AND pl.NurseryId = zn.NurseryId AND pl.IsSync = '1' AND pl.ZoneId ='" + zoneId + "'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new CustomData(cursor.getString(0), cursor.getString(1)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get details of routine visit nursery for display">
    public List<VisitNurseryViewData> GetVisitNurseryViewDetail(String visitUniqueId) {
        List<VisitNurseryViewData> labels = new ArrayList<VisitNurseryViewData>();
        selectQuery = "SELECT DISTINCT vr.VisitUniqueId, nu.NurseryType, nu.Title AS Nursery, zn.Title AS Zone, va.Title ||' - '||REPLACE(pl.PlantationDate,'/','-') AS Plantation, vr.PlantHeight, ps.Title AS PlantStatus, vr.PlantStatusId FROM VisitReport vr LEFT OUTER JOIN Nursery nu ON vr.FarmBlockNurseryUniqueId = nu.Id LEFT OUTER JOIN NurseryZone zn ON vr.ZoneId = zn.Id LEFT OUTER JOIN NurseryPlantation pl ON vr.PlantationId = pl.PlantationUniqueId LEFT OUTER JOIN Variety va ON va.Id = pl.CropVarietyId LEFT OUTER JOIN PlantStatus ps ON vr.PlantStatusId = ps.Id WHERE FarmBlockNurseryType = 'Nursery' AND vr.VisitUniqueId = '" + visitUniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new VisitNurseryViewData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of routine visit nursery for display">
    public List<VisitNurseryData> getVisitNurseryList() {
        //LOWER(nu.NurseryType), LOWER(nu.Title), LOWER(zn.Title), LOWER(va.Title)
        db.execSQL("DELETE FROM VisitReportPhoto WHERE VisitUniqueId IN (SELECT VisitUniqueId FROM VisitReport WHERE IsSync = '1' AND CreateDate < DATE('now'));");
        db.execSQL("DELETE FROM VisitReportDetail WHERE VisitUniqueId IN (SELECT VisitUniqueId FROM VisitReport WHERE IsSync = '1' AND CreateDate < DATE('now'));");
        db.execSQL("DELETE FROM VisitReport WHERE IsSync = '1' AND CreateDate < DATE('now');");
        List<VisitNurseryData> labels = new ArrayList<VisitNurseryData>();
        selectQuery = "SELECT DISTINCT vr.VisitUniqueId, nu.NurseryType, nu.Title AS Nursery, zn.Title AS Zone, va.Title ||' - '||REPLACE(pl.PlantationDate,'/','-') AS Plantation, vr.CreateDate FROM VisitReport vr LEFT OUTER JOIN Nursery nu ON vr.FarmBlockNurseryUniqueId = nu.Id LEFT OUTER JOIN NurseryZone zn ON vr.ZoneId = zn.Id LEFT OUTER JOIN NurseryPlantation pl ON vr.PlantationId = pl.PlantationUniqueId LEFT OUTER JOIN Variety va ON va.Id = pl.CropVarietyId WHERE FarmBlockNurseryType = 'Nursery' ORDER BY vr.Id DESC";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new VisitNurseryData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Routine Visit for Display">
    // LOWER(fa.FirstName), LOWER(fa.MiddleName), LOWER(fa.LastName), LOWER(fa.Mobile), LOWER(fb.FarmBlockCode), LOWER(va.Title)
    public List<VisitData> getVisitList() {
        db.execSQL("DELETE FROM VisitReportPhoto WHERE VisitUniqueId IN (SELECT VisitUniqueId FROM VisitReport WHERE IsSync = '1' AND CreateDate < DATE('now'));");
        db.execSQL("DELETE FROM VisitReportDetail WHERE VisitUniqueId IN (SELECT VisitUniqueId FROM VisitReport WHERE IsSync = '1' AND CreateDate < DATE('now'));");
        db.execSQL("DELETE FROM VisitReport WHERE IsSync = '1' AND CreateDate < DATE('now');");
        List<VisitData> labels = new ArrayList<VisitData>();
        selectQuery = "SELECT DISTINCT vr.VisitUniqueId, vr.FarmerUniqueId, vr.FarmBlockNurseryUniqueId, vr.PlantationId, fb.FarmBlockCode, fa.Salutation||' '||fa.FirstName||(CASE WHEN fa.MiddleName='' THEN '' ELSE ' '|| fa.MiddleName END)||' '|| fa.LastName AS Farmer, fa.Mobile, fa.FarmerCode, va.Title ||' - '||REPLACE(pl.PlantationDate,'/','-') AS Plantation, vr.CreateDate FROM VisitReport vr LEFT OUTER JOIN Farmer fa ON vr.FarmerUniqueId = fa.FarmerUniqueId LEFT OUTER JOIN FarmBlock fb ON vr.FarmBlockNurseryUniqueId= fb.FarmBlockUniqueId LEFT OUTER JOIN FarmerPlantation pl ON vr.PlantationId = pl.PlantationUniqueId LEFT OUTER JOIN Variety va ON va.Id = pl.CropVarietyId WHERE FarmBlockNurseryType = 'FarmBlock' ORDER BY vr.Id DESC";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new VisitData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get file details from VisitReportPhoto Table">
    public ArrayList<HashMap<String, String>> GetVisitReportPhoto(String visitUniqueId, String defectId) {
        wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT FilePath FROM VisitReportPhoto WHERE VisitUniqueId = '" + visitUniqueId + "' AND DefectId = '" + defectId + "';";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            map.put("FilePath", cursor.getString(0));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if defect is already added">
    public Boolean isExistDefectVisitReportDetail(String visitUniqueId, String defectId) {
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM VisitReportDetail WHERE VisitUniqueId = '" + visitUniqueId + "' AND DefectId = '" + defectId + "';";
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get count of loan details by Farmer Unique Id">
    public int loanDetailCount(String farmerUniqueId) {
        int loanCount = 0;
        selectQuery = "SELECT * FROM FarmerLoanDetailsTemp WHERE FarmerUniqueId = '" + farmerUniqueId + "';";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            loanCount = cursor.getCount();
        }
        cursor.close();
        return loanCount;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get count of family details by Farmer Unique Id">
    public int familyDetailCount(String farmerUniqueId) {
        int familyCount = 0;
        selectQuery = "SELECT * FROM FarmerFamilyMemberTemp WHERE FarmerUniqueId = '" + farmerUniqueId + "';";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            familyCount = cursor.getCount();
        }
        cursor.close();
        return familyCount;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Nursery Plantation for Display">
    public List<NurseryPlantationData> getNurseryPlantationList(String nurseryId, String zoneId) {
        List<NurseryPlantationData> labels = new ArrayList<>();

        selectQuery = "SELECT DISTINCT " +
                "pl.PlantationUniqueId, " +
                "pl.PlantationCode, " +
                "cr.Title AS Crop, " +
                "pt.Title AS PlantType, " +
                "pl.PlantationDate " +
                "FROM NurseryPlantation pl, PlantType pt, Crop cr " +
                "WHERE " +
                "pt.Id = pl.PlantTypeId " +
                "AND cr.Id = pl.CropId AND pl.NurseryId= '" + nurseryId + "' AND pl.ZoneId= '" + zoneId + "' ";

        cursor = db.rawQuery(selectQuery, null);

        while (cursor.moveToNext()) {
            NurseryPlantationData data = new NurseryPlantationData();
            data.setPlantationUniqueId(cursor.getString(0));
            data.setPlantationCode(cursor.getString(1));
            data.setCrop(cursor.getString(2));
            data.setPlantType(cursor.getString(3));
            data.setPlantationDate(cursor.getString(4));
            labels.add(data);
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if same additional activity is already added added">
    public Boolean isActivityAlreadyAdded(String activityId, String subActivityId) {
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM JobCardDetailTemp WHERE FarmActivityId = '" + activityId + "' AND FarmSubActivityId ='" + subActivityId + "' AND ActivityType ='A'  ";
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if file Is uploaded for activity">
    public Boolean isFileAlreadeUploaded(String activityId, String subActivityId, String type) {
        Boolean dataExists = false;
        selectQuery = "SELECT Type FROM TempJobCardFile WHERE Type ='" + type + "' AND ActivityId='" + activityId + "' AND SubActivityId='" + subActivityId + "'   ";
        Log.e("selectQuery", selectQuery);
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="Code to Job Card Unique Id by Plantation Unique Id and FarmBlock Unique Id">
    public String getJobCardUniqueId(String plantationUniqueId, String farmBlockNurseryUniqueId) {
        String jobCardUniqueId = "";
        selectQuery = "SELECT JobCardUniqueId FROM JobCard WHERE PlantationUniqueId= '" + plantationUniqueId + "' AND FarmBlockNurseryUniqueId= '" + farmBlockNurseryUniqueId + "' AND SUBSTR(VisitDate,9,2) =SUBSTR(DATE('now'),9,2) AND SUBSTR(VisitDate,6,2) =SUBSTR(DATE('now'),6,2) AND SUBSTR(VisitDate,1,4) =SUBSTR(DATE('now'),1,4)";


        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                jobCardUniqueId = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        return jobCardUniqueId;
    }
    //</editor-fold>

    //<editor-fold desc="To get List Of Job Cards For Farm Blocks">
    public ArrayList<HashMap<String, String>> getJobCardList() {
        wordList = new ArrayList<HashMap<String, String>>();

        selectQuery = "SELECT jc.JobCardUniqueId, jc.VisitDate, fb.FarmBlockCode,var.Title||' - '|| pl.PlantationDate FROM JobCard jc, FarmBlock fb, FarmerPlantation pl, Variety var, Crop cr WHERE jc.FarmBlockNurseryUniqueId = fb.FarmBlockUniqueId AND jc.PlantationUniqueId = pl.PlantationUniqueId AND pl.CropVarietyId = var.Id AND pl.CropId = cr.Id AND jc.FarmBlockNurseryType='FarmBlock' ORDER BY jc.VisitDate";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("JobCardUniqueId", cursor.getString(0));
            map.put("VisitDate", cursor.getString(1));
            map.put("FarmBlockCode", cursor.getString(2));
            map.put("Plantation", cursor.getString(3));
            wordList.add(map);
        }

        cursor.close();

        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get List Of Job Cards For Nursery">
    public ArrayList<HashMap<String, String>> getJobCardListForNursery() {
        wordList = new ArrayList<HashMap<String, String>>();

        selectQuery = "SELECT jc.JobCardUniqueId, jc.VisitDate, nr.Title,var.Title||' - '|| pl.PlantationDate, nz.Title FROM JobCard jc, Nursery nr,NurseryZone nz, NurseryPlantation pl, Variety var, Crop cr WHERE jc.FarmBlockNurseryUniqueId = nr.UniqueId AND jc.ZoneId = nz.Id AND jc.PlantationUniqueId = pl.PlantationUniqueId AND pl.CropVarietyId = var.Id AND pl.CropId = cr.Id AND jc.FarmBlockNurseryType='Nursery' ORDER BY jc.VisitDate";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("JobCardUniqueId", cursor.getString(0));
            map.put("VisitDate", cursor.getString(1));
            map.put("Nursery", cursor.getString(2));
            map.put("Plantation", cursor.getString(3));
            map.put("Zone", cursor.getString(4));
            wordList.add(map);
        }

        cursor.close();

        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Visit Report For Sync">
    public ArrayList<HashMap<String, String>> GetVisitReportSync() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT FarmBlockNurseryType, FarmBlockNurseryUniqueId, ZoneId, PlantationId, CreateDate, PlantHeight, PlantStatusId, Latitude, Longitude, Accuracy, CreateBy, VisitUniqueId, CASE WHEN Days ='' THEN 0 ELSE Days END FROM VisitReport WHERE IsSync IS NULL AND IsTemp = '0'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmBlockNurseryType", cursor.getString(0));
            map.put("FarmBlockNurseryId", cursor.getString(1));
            map.put("ZoneId", cursor.getString(2));
            map.put("PlantationId", cursor.getString(3));
            map.put("VisitDate", cursor.getString(4));
            map.put("PlantHeight", cursor.getString(5));
            map.put("PlantStatusId", cursor.getString(6));
            map.put("Latitude", cursor.getString(7));
            map.put("Longitude", cursor.getString(8));
            map.put("Accuracy", cursor.getString(9));
            map.put("UserId", cursor.getString(10));
            map.put("VisitUniqueId", cursor.getString(11));
            map.put("NoOfDays", cursor.getString(12));
            list.add(map);
        }
        cursor.close();
        return list;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Visit Report For Sync">
    public ArrayList<HashMap<String, String>> GetVisitReportDetailSync() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT a.VisitUniqueId, a.UniqueId, a.DefectId, a.Remarks, IFNULL(b.FileName,'') FROM VisitReportDetail a LEFT OUTER JOIN VisitReportPhoto b ON a.VisitUniqueId = b.VisitUniqueId AND a.DefectId = b.DefectId WHERE a.IsSync IS NULL AND a.IsTemp = '0'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("VisitUniqueId", cursor.getString(0));
            map.put("UniqueId", cursor.getString(1));
            map.put("DefectId", cursor.getString(2));
            map.put("Remarks", cursor.getString(3));
            map.put("FileName", cursor.getString(4));
            list.add(map);
        }
        cursor.close();
        return list;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if Coordinates are already Added in Temporary Nursery Table">
    public Boolean TempNurseryGPSExists(String latitude, String longitude) {
        Boolean dataExists = false;

        selectQuery = "SELECT * FROM TempNurseryGPS WHERE Latitude = '" + latitude + "' AND Longitude ='" + longitude + "'  ";

        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get Coordinates from Temporary Table">
    public List<CustomCoord> GetTempNurseryGPS() {
        List<CustomCoord> gps = new ArrayList<CustomCoord>();
        selectQuery = "SELECT Id, Latitude, Longitude,Accuracy FROM TempNurseryGPS ORDER BY Id";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            gps.add(new CustomCoord(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }
        cursor.close();
        return gps;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get Coordinates from NurseryCoordinates Table">
    public List<CustomCoord> GetNurseryCoordinates(String nurseryId, String nurseryZoneId) {
        List<CustomCoord> gps = new ArrayList<CustomCoord>();
        selectQuery = "SELECT Id, Latitude, Longitude,Accuracy FROM NurseryCoordinates WHERE NurseryId =  '" + nurseryId + "' AND NurseryZoneId =  '" + nurseryZoneId + "' ORDER BY Id";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            gps.add(new CustomCoord(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }
        cursor.close();
        return gps;

    }
    //</editor-fold>

    //<editor-fold desc="To get all New Coordinates For Sync">
    public ArrayList<HashMap<String, String>> getNurseryZoneCoordinates() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = " SELECT Id, UniqueId, NurseryId, NurseryZoneId, Latitude, Longitude, Accuracy, CreateBy, CreateDate FROM NurseryCoordinates WHERE IsSync = 0";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("Id", cursor.getString(0));
            map.put("UniqueId", cursor.getString(1));
            map.put("NurseryId", cursor.getString(2));
            map.put("NurseryZoneId", cursor.getString(3));
            map.put("Latitude", cursor.getString(4));
            map.put("Longitude", cursor.getString(5));
            map.put("Accuracy", cursor.getString(6));
            map.put("CreateBy", cursor.getString(7));
            map.put("CreateDate", cursor.getString(8));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if farm block coordinates is already added">
    public Boolean isExistNurseryCoordinates(String nurseryId, String nurseryZoneId) {
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM NurseryCoordinates WHERE NurseryId = '" + nurseryId + "' AND NurseryZoneId = '" + nurseryZoneId + "'";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Nurseries for Display">
    public List<NurseryData> getNurseryList() {
        List<NurseryData> labels = new ArrayList<NurseryData>();

        selectQuery = "SELECT ns.NurseryType, ns.Id, ns.UniqueId, ns.Title, ns.KhataNo, ns.KhasraNo, (CASE WHEN ad.AddressType = 'District Based' THEN vi.VillageName||' , '||bl.BlockName ELSE ci.CityName ||' , '|| di.DistrictName END) AS Address, ns.Area FROM Nursery ns, AddressData ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId WHERE ns.UniqueId = ad.UniqueId ORDER BY ns.NurseryType, ns.Title COLLATE NOCASE ASC";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new NurseryData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Method to insert Update Farmer Address Details from Server in Address Table">
    public String insertServerNurseryAddress(String uniqueId, String street1, String street2, String stateId, String districtId, String blockId, String panchayatId, String villageId, String cityId, String pinCodeId, String addressType) {
        result = "fail";

        Boolean dataExists = false;
        selectQuery = "SELECT UniqueId FROM AddressData WHERE UniqueId = '" + uniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        if (dataExists.equals(false)) {
            newValues = new ContentValues();

            newValues.put("UniqueId", uniqueId);
            newValues.put("AddressType", addressType);
            newValues.put("Street1", street1);
            newValues.put("Street2", street2);
            newValues.put("StateId", stateId);
            newValues.put("DistrictId", districtId);
            newValues.put("BlockId", blockId);
            newValues.put("PanchayatId", panchayatId);
            newValues.put("VillageId", villageId);
            newValues.put("CityId", cityId);
            newValues.put("PinCodeId", pinCodeId);
            db = dbHelper.getWritableDatabase();
            db.insert("AddressData", null, newValues);
        } else {
            selectQuery = "UPDATE AddressData SET Street2= '" + street2 + "',Street1= '" + street1 + "',StateId= '" + stateId + "',DistrictId= '" + districtId + "',BlockId= '" + blockId + "',PanchayatId= '" + panchayatId + "',VillageId= '" + villageId + "',CityId= '" + cityId + "',PinCodeId= '" + pinCodeId + "',AddressType= '" + addressType + "' WHERE UniqueId ='" + uniqueId + "' ";
            db.execSQL(selectQuery);
        }
        result = "success";
        return result;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get nursery details by nursery unique id">
    public List<NurseryViewData> getNurseryById(String nurseryUniqueId) {
        List<NurseryViewData> labels = new ArrayList<NurseryViewData>();
        selectQuery = "SELECT DISTINCT ns.Id, ns.UniqueId, ns.Title, ns.NurseryType, ns.Salutation||' '||ns.FirstName||(CASE WHEN ns.MiddleName= '' THEN '' ELSE ' '|| ns.MiddleName END)||' '|| ns.LastName AS ContactPerson, ns.Mobile, ifnull(ns.AlternateMobile,'') As AlternateMobile, ns.EmailId, ns.LandType, adr.Street1, adr.Street2, st.StateName, di.DistrictName, bl.BlockName, pn.PanchayatName, vi.VillageName, ifnull(pi.PinCode,'') As PinCode, adr.AddressType, ns.RegistryDate, ns.OfficePrimise, ns.GSTNo, ns.GSTDate, ns.OwnerName, ns.LoginId, ns.ContactNo, ifnull(ns1.Title, '') As MainNursery, ifnull(ns.CertifiedBy, '') As CertifiedBy, ifnull(ns.RegistrationNo, '') As RegistrationNo, ns.RegistrationDate, ns.KhataNo, ns.KhasraNo, ns.ContractDate, ns.Area, ifnull(so.Title,'') AS SoilType, ifnull(ns.ElevationMSL,'') AS ElevationMSL, ns.SoilPH, ifnull(ns.Nitrogen, '') AS Nitrogen, ifnull(ns.Potash, ''), ifnull(ns.Phosphorus, ''), ifnull(ns.OrganicCarbonPerc, ''), ifnull(ns.Magnesium, ''), ifnull(ns.Calcium, ''), ifnull(eu.Title,'') AS ExistingUse, ifnull(cu.Title ,'') AS CommunityUse, ifnull(eh.Title,'') AS ExistingHazard, ifnull(nr.Title,'') AS River, ifnull(nd.Title,'') AS Dam, ifnull(ir.Title,'') AS Irrigation, ifnull(ns.OverheadTransmission, ''), ifnull(ld.Title,'') AS LegalDispute, ifnull(ws.Title,'') AS SourceWater, ifnull(es.title,'') AS ElectricitySource, ifnull(ns.DripperSpacing, ''), ifnull(ns.DischargeRate, ''),  ifnull(ci.CityName,'') FROM Nursery ns LEFT OUTER JOIN SoilType so ON ns.SoilTypeId = so.Id LEFT OUTER JOIN ExistingUse eu ON ns.ExistingUseId =eu.Id LEFT OUTER JOIN CommunityUse cu ON ns.CommunityUseId = cu.Id LEFT OUTER JOIN ExistingHazard eh ON ns.ExistingHazardId = eh.Id LEFT OUTER JOIN  NearestRiver nr ON ns.RiverId = nr.Id LEFT OUTER JOIN NearestDam nd ON ns.DamId = nd.Id LEFT OUTER JOIN IrrigationSystem ir ON ns.IrrigationId = ir.Id LEFT OUTER JOIN LegalDispute ld ON ns.LegalDisputeId = ld.Id LEFT OUTER JOIN WaterSource ws ON ns.SourceWaterId = ws.id LEFT OUTER JOIN ElectricitySource es ON ns.ElectricitySourceId = es.Id LEFT OUTER JOIN  AddressData adr ON ns.UniqueId = adr.UniqueId LEFT OUTER JOIN State st ON adr.StateId = st.StateId LEFT OUTER JOIN District di ON adr.DistrictId = di.DistrictId LEFT OUTER JOIN Block bl ON adr.BlockId = bl.BlockId LEFT OUTER JOIN  Panchayat pn ON adr.PanchayatId = pn.PanchayatId LEFT OUTER JOIN Village vi ON adr.VillageId = vi.VillageId LEFT OUTER JOIN PinCode pi ON adr.PinCodeId = pi.PinCodeId LEFT OUTER JOIN City ci ON adr.CityId = ci.CityId  LEFT OUTER JOIN Nursery ns1 ON ns1.Id = ns.MainNurseryId WHERE ns.UniqueId =  '" + nurseryUniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new NurseryViewData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14), cursor.getString(15), cursor.getString(16), cursor.getString(17), cursor.getString(18), cursor.getString(19), cursor.getString(20), cursor.getString(21), cursor.getString(22), cursor.getString(23), cursor.getString(24), cursor.getString(25), cursor.getString(26), cursor.getString(27), cursor.getString(28), cursor.getString(29), cursor.getString(30), cursor.getString(31), cursor.getString(32), cursor.getString(33), cursor.getString(34), cursor.getString(35), cursor.getString(36), cursor.getString(37), cursor.getString(38), cursor.getString(39), cursor.getString(40), cursor.getString(41), cursor.getString(42), cursor.getString(43), cursor.getString(44), cursor.getString(45), cursor.getString(46), cursor.getString(47), cursor.getString(48), cursor.getString(49), cursor.getString(50), cursor.getString(51), cursor.getString(52), cursor.getString(53), cursor.getString(54)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="To view nursery mini account details by id">
    public ArrayList<HashMap<String, String>> GetNurseryMiniAccount(String id) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT na.AccountNo, na.IFSCCode  FROM NurseryAccountDetail na, Nursery nu WHERE na.NurseryId = nu.Id AND na.NurseryId ='" + id + "'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("Account", cursor.getString(0));
            map.put("IFSC", cursor.getString(1));
            list.add(map);
        }
        cursor.close();
        return list;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Nurseries Zone for Display">
    public List<NurseryZoneData> getNurseryZoneList(String nurseryId) {
        List<NurseryZoneData> labels = new ArrayList<NurseryZoneData>();
        selectQuery = "SELECT nz.NurseryId, nz.Id, nz.UniqueId, nz.Title, nz.Area FROM Nursery ns, NurseryZone nz WHERE ns.Id = nz.NurseryId AND nz.NurseryId =   '" + nurseryId + "' ORDER BY nz.Title COLLATE NOCASE ASC";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new NurseryZoneData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="To view nursery zone by id">
    public ArrayList<HashMap<String, String>> GetNurseryZoneViewById(String id) {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT nu.Id AS NurseryId, zn.Id AS ZoneId, nu.NurseryType, nu.Title AS Nursery, zn.Title AS Zone, zn.Area FROM Nursery nu, NurseryZone zn WHERE nu.Id = zn.NurseryId AND zn.Id ='" + id + "'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("NurseryId", cursor.getString(0));
            map.put("ZoneId", cursor.getString(1));
            map.put("NurseryType", cursor.getString(2));
            map.put("Nursery", cursor.getString(3));
            map.put("Zone", cursor.getString(4));
            map.put("Area", cursor.getString(5));
            list.add(map);
        }
        cursor.close();
        return list;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Land Characteristic Details by Nursery Unique Id">
    public List<LandCharacteristic> getLandCharacteristicCheckedByNurseryUniqueId(String nurseryUniqueId) {
        List<LandCharacteristic> labels = new ArrayList<LandCharacteristic>();

        selectQuery = "SELECT DISTINCT lc.Id, lc.Title, ifnull(nlc.LandCharacteristicId, 0) FROM LandCharacteristic lc, NurseryLandCharacteristic nlc, Nursery ns WHERE ns.Id = nlc.NurseryId AND lc.Id = nlc.LandCharacteristicId AND ns.UniqueId ='" + nurseryUniqueId + "' ORDER BY lc.Title  COLLATE NOCASE ASC ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new LandCharacteristic(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Land Issue Details by Nursery Unique Id">
    public List<LandCharacteristic> getLandIssueCheckedByNurseryUniqueId(String nurseryUniqueId) {
        List<LandCharacteristic> labels = new ArrayList<LandCharacteristic>();

        selectQuery = "SELECT DISTINCT li.Id, li.Title, ifnull(nli.LandIssueId,0) FROM LandIssue li, NurseryLandIssue nli, Nursery ns WHERE ns.Id = nli.NurseryId AND li.Id = nli.LandIssueId AND ns.UniqueId  ='" + nurseryUniqueId + "' ORDER BY li.Title  COLLATE NOCASE ASC ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new LandCharacteristic(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Nursery Cropping Pattern by Nursery Unique Id">
    public List<NurseryCroppingPatternData> getNurseryCroppingPatternByUniqueId(String nurseryUniqueId) {
        List<NurseryCroppingPatternData> labels = new ArrayList<NurseryCroppingPatternData>();

        selectQuery = "SELECT  DISTINCT ncp.Id, ns.UniqueId, v.CropId, c.Title AS Crop, ncp.CropVarietyId, v.Title AS CropVariety, ncp.SeasonId, s.Title AS Season, ncp.acreage, ncp.FinancialYear FROM NurseryCroppingPattern ncp, Crop c, Variety v, Season s, Nursery ns WHERE ncp.NurseryId = ns.Id AND v.CropId = c.Id AND ncp.CropVarietyId = v.Id AND ncp.SeasonId = s.Id AND ns.UniqueId ='" + nurseryUniqueId + "' ";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new NurseryCroppingPatternData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="To get Planned Activities by Job Card Unique Id">
    public ArrayList<HashMap<String, String>> getPlannedActivities(String jobCardUniqueId) {
        wordList = new ArrayList<HashMap<String, String>>();

        //selectQuery = "SELECT DISTINCT pl.WeekNO, fa.Title,ifnull(fs.SubActivityName,''), pl.Quantity, uom.ShortName, pl.Remarks, ifnull(jd.ActivityValue,''),ifnull(jd.UniqueId,''),ifnull(jd.FileName,'') FROM JobCard jc,JobCardDetail jd  LEFT OUTER JOIN FarmSubActivity fs ON jd.FarmSubActivityId = fs.FarmSubActivityId,PlannedActivity pl, FarmActivity fa,UOM uom WHERE jc.JobCardUniqueId='" + jobCardUniqueId + "' AND jc.JobCardUniqueId = jd.JobCardUniqueId AND jd.ActivtyType='P' AND jc.PlantationUniqueId = pl.PlantationUniqueId AND (CAST((strftime('%s',pl.FromDate)-strftime('%s',DATE('now'))) AS int)/60/60/24) <= 0 AND (CAST((strftime('%s',pl.ToDate)-strftime('%s',DATE('now'))) AS int)/60/60/24)>= 0 AND pl.ActivityId = fa.FarmActivityId AND pl.UOMId = uom.UOMId AND pl.SubActivityId = jd.FarmSubActivityId ORDER BY fa.Title  COLLATE NOCASE ASC,fs.SubActivityName COLLATE NOCASE ASC";
        selectQuery = "SELECT DISTINCT pl.WeekNO, fa.Title,ifnull(fs.SubActivityName,''), pl.Quantity, uom.ShortName, pl.Remarks, ifnull(jd.ActivityValue,''),ifnull(jd.UniqueId,''),ifnull(jd.FileName,''), ifnull(detsum.TotalValue,'') FROM JobCard jc,JobCardDetail jd  LEFT OUTER JOIN FarmSubActivity fs ON jd.FarmSubActivityId = fs.FarmSubActivityId,PlannedActivity pl LEFT OUTER JOIN (SELECT jc.PlantationUniqueId,jc.WeekNo, jcd.FarmActivityId, jcd.FarmSubActivityId, SUM(jcd.ActivityValue) AS TotalValue FROM JobCard jc, JobCardDetail jcd WHERE jc.JobCardUniqueId = jcd.JobCardUniqueId AND jcd.ActivtyType ='P' AND jcd.ActivityValue !='' GROUP BY jc.PlantationUniqueId,jcd.FarmActivityId, jcd.FarmSubActivityId ) detsum ON pl.ActivityId = detsum.FarmActivityId AND pl.SubActivityId =detsum.FarmSubActivityId AND pl.WeekNO = detsum.WeekNo AND pl.PlantationUniqueId =detsum.PlantationUniqueId , FarmActivity fa,UOM uom WHERE jc.JobCardUniqueId='" + jobCardUniqueId + "'  AND jc.JobCardUniqueId = jd.JobCardUniqueId AND jd.ActivtyType='P' AND jc.PlantationUniqueId = pl.PlantationUniqueId AND (CAST((strftime('%s',pl.FromDate)-strftime('%s',DATE('now'))) AS int)/60/60/24) <= 0 AND (CAST((strftime('%s',pl.ToDate)-strftime('%s',DATE('now'))) AS int)/60/60/24)>= 0 AND pl.ActivityId = fa.FarmActivityId AND pl.UOMId = uom.UOMId AND pl.SubActivityId = jd.FarmSubActivityId ORDER BY fa.Title  COLLATE NOCASE ASC,fs.SubActivityName COLLATE NOCASE ASC";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("WeekNo", cursor.getString(0));
            map.put("ActivityName", cursor.getString(1));
            map.put("SubActivityName", cursor.getString(2));
            map.put("Quantity", cursor.getString(3));
            map.put("UOM", cursor.getString(4));
            map.put("Remarks", cursor.getString(5));
            map.put("ActualQty", cursor.getString(6));
            map.put("UniqueId", cursor.getString(7));
            map.put("FileName", cursor.getString(8));
            map.put("TotalValue", cursor.getString(9));
            wordList.add(map);
        }

        cursor.close();

        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get Recommended Activities by Job Card Unique Id">
    public ArrayList<HashMap<String, String>> getRecommendedActivities(String jobCardUniqueId) {
        wordList = new ArrayList<HashMap<String, String>>();

        //selectQuery = "SELECT DISTINCT pl.WeekNO, fa.Title,ifnull(fs.SubActivityName,''), pl.Quantity, uom.ShortName, pl.Remarks, ifnull(jd.ActivityValue,''),ifnull(jd.UniqueId,''),ifnull(jd.FileName,'') FROM JobCard jc,JobCardDetail jd  LEFT OUTER JOIN FarmSubActivity fs ON jd.FarmSubActivityId = fs.FarmSubActivityId,RecommendedActivity pl, FarmActivity fa,UOM uom WHERE jc.JobCardUniqueId='" + jobCardUniqueId + "' AND jc.JobCardUniqueId = jd.JobCardUniqueId AND jd.ActivtyType='R' AND jc.PlantationUniqueId = pl.PlantationUniqueId AND (CAST((strftime('%s',pl.FromDate)-strftime('%s',DATE('now'))) AS int)/60/60/24) <= 0 AND (CAST((strftime('%s',pl.ToDate)-strftime('%s',DATE('now'))) AS int)/60/60/24)>= 0 AND pl.ActivityId = fa.FarmActivityId AND pl.UOMId = uom.UOMId AND pl.SubActivityId = jd.FarmSubActivityId";
        selectQuery = "SELECT DISTINCT pl.WeekNO, fa.Title,ifnull(fs.SubActivityName,''), pl.Quantity, uom.ShortName, pl.Remarks, ifnull(jd.ActivityValue,''),ifnull(jd.UniqueId,''),ifnull(jd.FileName,''), ifnull(detsum.TotalValue,'') FROM JobCard jc,JobCardDetail jd  LEFT OUTER JOIN FarmSubActivity fs ON jd.FarmSubActivityId = fs.FarmSubActivityId,RecommendedActivity pl LEFT OUTER JOIN (SELECT jc.PlantationUniqueId, jc.WeekNo, jcd.FarmActivityId, jcd.FarmSubActivityId, SUM(jcd.ActivityValue) AS TotalValue FROM JobCard jc, JobCardDetail jcd WHERE jc.JobCardUniqueId = jcd.JobCardUniqueId AND jcd.ActivtyType ='R'  AND jcd.ActivityValue !='' GROUP BY jcd.FarmActivityId, jcd.FarmSubActivityId ) detsum ON pl.ActivityId = detsum.FarmActivityId AND pl.SubActivityId =detsum.FarmSubActivityId AND pl.WeekNO = detsum.WeekNo AND pl.PlantationUniqueId =detsum.PlantationUniqueId , FarmActivity fa,UOM uom WHERE jc.JobCardUniqueId='" + jobCardUniqueId + "' AND jc.JobCardUniqueId = jd.JobCardUniqueId AND jd.ActivtyType='R' AND jc.PlantationUniqueId = pl.PlantationUniqueId AND (CAST((strftime('%s',pl.FromDate)-strftime('%s',DATE('now'))) AS int)/60/60/24) <= 0 AND (CAST((strftime('%s',pl.ToDate)-strftime('%s',DATE('now'))) AS int)/60/60/24)>= 0 AND pl.ActivityId = fa.FarmActivityId AND pl.UOMId = uom.UOMId AND pl.SubActivityId = jd.FarmSubActivityId  ORDER BY fa.Title  COLLATE NOCASE ASC,fs.SubActivityName COLLATE NOCASE ASC";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("WeekNo", cursor.getString(0));
            map.put("ActivityName", cursor.getString(1));
            map.put("SubActivityName", cursor.getString(2));
            map.put("Quantity", cursor.getString(3));
            map.put("UOM", cursor.getString(4));
            map.put("Remarks", cursor.getString(5));
            map.put("ActualQty", cursor.getString(6));
            map.put("UniqueId", cursor.getString(7));
            map.put("FileName", cursor.getString(8));
            map.put("TotalValue", cursor.getString(9));
            wordList.add(map);
        }

        cursor.close();

        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get Unplanned Activities by Job Card Unique Id">
    public ArrayList<HashMap<String, String>> getUnplannedActivities(String jobCardUniqueId) {
        wordList = new ArrayList<HashMap<String, String>>();

        selectQuery = "SELECT DISTINCT fa.Title,ifnull(fs.SubActivityName,''), ifnull(uom.ShortName, uom1.ShortName), ifnull(jd.ActivityValue,''),ifnull(jd.UniqueId,''),ifnull(jd.FileName,'') FROM JobCard jc,JobCardDetail jd  LEFT OUTER JOIN FarmSubActivity fs ON jd.FarmSubActivityId = fs.FarmSubActivityId LEFT OUTER JOIN UOM uom1 ON fs.UomId = uom1.UomId, FarmActivity fa LEFT OUTER JOIN UOM uom ON fa.UomId = uom.UomId WHERE jc.JobCardUniqueId='" + jobCardUniqueId + "' AND jc.JobCardUniqueId = jd.JobCardUniqueId AND jd.ActivtyType='A' AND jd.FarmActivityId = fa.FarmActivityId  AND jd.FarmSubActivityId = jd.FarmSubActivityId";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("ActivityName", cursor.getString(0));
            map.put("SubActivityName", cursor.getString(1));
            map.put("UOM", cursor.getString(2));
            map.put("ActualQty", cursor.getString(3));
            map.put("UniqueId", cursor.getString(4));
            map.put("FileName", cursor.getString(5));
            wordList.add(map);
        }

        cursor.close();

        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all unsync Job Cards">
    public ArrayList<HashMap<String, String>> getUnSyncJobCards() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT JobCardUniqueId, FarmBlockNurseryType,FarmBlockNurseryUniqueId, ZoneId, PlantationUniqueId, WeekNo, VisitDate, Longitude,Latitude,Accuracy,CreatBy, CreateDate,ifnull(CreateByRole,'') FROM JobCard WHERE IsSync IS NULL OR IsSync='' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("UniqueId", cursor.getString(0));
            map.put("FBNurseryType", cursor.getString(1));
            map.put("FBNurseryUniqueId", cursor.getString(2));
            map.put("ZoneId", cursor.getString(3));
            map.put("PlantationUniqueId", cursor.getString(4));
            map.put("WeekNo", cursor.getString(5));
            map.put("VisitDate", cursor.getString(6));
            map.put("Longitude", cursor.getString(7));
            map.put("Latitude", cursor.getString(8));
            map.put("Accuracy", cursor.getString(9));
            map.put("CreateBy", cursor.getString(10));
            map.put("AndroidDate", cursor.getString(11));
            map.put("CreateByRole", cursor.getString(12));

            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all unsync Job Card Details">
    public ArrayList<HashMap<String, String>> getUnSyncJobCardDetail() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT jd.JobCardUniqueId, jd.FarmActivityId, (CASE WHEN jd.FarmSubActivityId ='null' THEN 0 ELSE jd.FarmSubActivityId END), (CASE WHEN jd.ActivityValue='' THEN '0' ELSE jd.ActivityValue END), jd.ActivtyType, jd.PlannerDetailId, jd.ParameterDetailId, jd.UniqueId, ifnull(jd.FileName,'') FROM JobCard jc, JobCardDetail jd WHERE (jc.IsSync IS NULL OR jc.IsSync='') AND jc.JobCardUniqueId = jd.JobCardUniqueId ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("UniqueId", cursor.getString(0));
            map.put("FarmActivityId", cursor.getString(1));
            map.put("FarmSubActivityId", cursor.getString(2));
            map.put("ActivityValue", cursor.getString(3));
            map.put("ActivityType", cursor.getString(4));
            map.put("PlannerDetailId", cursor.getString(5));
            map.put("ParameterDetailId", cursor.getString(6));
            map.put("JCDUniqueId", cursor.getString(7));
            map.put("FileNames", cursor.getString(8));

            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all NurseryInterCropping For Sync">
    public ArrayList<HashMap<String, String>> getNurseryInterCroppingForSync() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<>();
        selectQuery = "SELECT InterCroppingUniqueId, PlantationUniqueId, NurseryId, CropVarietyId, Acreage, SeasonId," +
                "Longitude, Latitude, Accuracy, CreateBy, CreateDate " +
                "FROM NurseryInterCropping " +
                "WHERE IsSync IS NULL OR IsSync ='' ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            map.put("InterCroppingUniqueId", cursor.getString(0));
            map.put("PlantationUniqueId", cursor.getString(1));
            map.put("NurseryId", cursor.getString(2));
            map.put("CropVarietyId", cursor.getString(3));
            map.put("Acreage", cursor.getString(4));
            map.put("SeasonId", cursor.getString(5));
            map.put("Longitude", cursor.getString(6));
            map.put("Latitude", cursor.getString(7));
            map.put("Accuracy", cursor.getString(8));
            map.put("CreateBy", cursor.getString(9));
            map.put("CreateDate", cursor.getString(10));

            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Nursery PlantationUniqueId for which Plantation Code is not Available">
    public ArrayList<HashMap<String, String>> getNurseryPlantationUniqueId() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<>();
        selectQuery = "SELECT PlantationUniqueId, CreateBy FROM NurseryPlantation WHERE PlantationCode IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            map.put("PlantationUniqueId", cursor.getString(0));
            map.put("CreateBy", cursor.getString(1));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get Week Number By Plantation Unique Id">
    public String getWeekNoForPlantation(String plantationUniqueId) {
        String weekNo = "";
        selectQuery = "SELECT WeekNo FROM PlantationWeek WHERE PlantationUniqueId ='" + plantationUniqueId + "' AND (CAST((strftime('%s',FromDate)-strftime('%s',DATE('now'))) AS int)/60/60/24) <= 0 AND (CAST((strftime('%s',ToDate)-strftime('%s',DATE('now'))) AS int)/60/60/24)>= 0 ";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                weekNo = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        return weekNo;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Nursery Inter Cropping UniqueId for which Financial Year is not Available">
    public ArrayList<HashMap<String, String>> getNurseryInterCropFinUniqueId() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT InterCroppingUniqueId, CreateBy " +
                "FROM NurseryInterCropping WHERE FinancialYear IS NULL";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<>();
            map.put("InterCroppingUniqueId", cursor.getString(0));
            map.put("CreateBy", cursor.getString(1));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Recommendation FarmBlock for Display">
    public List<VisitNurseryData> GetRecommendationFarmBlockList() {
        db.execSQL("DELETE FROM RecommendationDetail WHERE RecommendationUniqueId IN (SELECT UniqueId FROM Recommendation WHERE IsSyncData = '1' AND CreateDate < DATE('now'));");
        db.execSQL("DELETE FROM Recommendation WHERE IsSyncData = '1' AND CreateDate < DATE('now');");
        List<VisitNurseryData> labels = new ArrayList<VisitNurseryData>();
        selectQuery = "SELECT DISTINCT re.UniqueId AS A, fa.Salutation||' '||fa.FirstName||(CASE WHEN fa.MiddleName='' THEN '' ELSE ' '|| fa.MiddleName END)||' '|| fa.LastName AS B, fa.Mobile AS C, fb.FarmBlockCode AS D, va.Title ||' - '||REPLACE(pl.PlantationDate,'/','-') AS E, re.CreateDate AS F FROM Recommendation re LEFT OUTER JOIN Farmer fa ON re.FarmerUniqueId = fa.FarmerUniqueId LEFT OUTER JOIN FarmBlock fb ON re.FarmBlockNurseryUniqueId= fb.FarmBlockUniqueId LEFT OUTER JOIN FarmerPlantation pl ON re.PlantationId = pl.PlantationUniqueId LEFT OUTER JOIN Variety va ON va.Id = pl.CropVarietyId WHERE FarmBlockNurseryType = 'FarmBlock' ORDER BY re.Id DESC";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new VisitNurseryData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>


    //<editor-fold desc="Code to get list of Recommendation Nursery for Display">
    public List<VisitNurseryData> GetRecommendationNurseryList() {
        db.execSQL("DELETE FROM RecommendationDetail WHERE RecommendationUniqueId IN (SELECT UniqueId FROM Recommendation WHERE IsSyncData = '1' AND CreateDate < DATE('now'));");
        db.execSQL("DELETE FROM Recommendation WHERE IsSyncData = '1' AND CreateDate < DATE('now');");
        List<VisitNurseryData> labels = new ArrayList<VisitNurseryData>();
        selectQuery = "SELECT DISTINCT re.UniqueId AS A, nu.NurseryType AS B, nu.Title AS C, zn.Title AS D, va.Title ||' - '||REPLACE(pl.PlantationDate,'/','-') AS E, re.CreateDate AS F FROM Recommendation re LEFT OUTER JOIN Nursery nu ON re.FarmBlockNurseryUniqueId = nu.Id LEFT OUTER JOIN NurseryZone zn ON re.ZoneId = zn.Id LEFT OUTER JOIN NurseryPlantation pl ON re.PlantationId = pl.PlantationUniqueId LEFT OUTER JOIN Variety va ON va.Id = pl.CropVarietyId WHERE FarmBlockNurseryType = 'Nursery' ORDER BY re.Id DESC";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new VisitNurseryData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="To get All Roles Comma Seperated">
    public String getAllRoles() {
        String userRoles = "";

        selectQuery = "SELECT DISTINCT RoleName FROM UserRoles";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            userRoles = userRoles + cursor.getString(0) + ",";
        }

        cursor.close();

        return userRoles;
    }
    //</editor-fold>

    //<editor-fold desc="To get all InterCroppingUniqueId for Inserting Into NurseryInterCropping Sync Table">
    public ArrayList<HashMap<String, String>> getNurseryInterCroppingUniqueUpdateId() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();

        selectQuery = "SELECT InterCroppingUniqueId FROM NurseryInterCroppingSyncTable";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("InterCroppingUniqueId", cursor.getString(0));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get Nursery Unique Id By Nursery Id">
    public String getNurseryUniqueId(String nurseryId) {
        String nurseryUniqueId = "";
        selectQuery = "SELECT UniqueId FROM Nursery WHERE Id ='" + nurseryId + "'";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                nurseryUniqueId = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        return nurseryUniqueId;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get Zone Area By Zone Id">
    public String getZoneAreaByZoneId(String zoneId) {
        String nurseryUniqueId = "";
        selectQuery = "SELECT Area FROM NurseryZone WHERE Id = '" + zoneId + "'";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                nurseryUniqueId = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        return nurseryUniqueId;
    }
    //</editor-fold>

    //<editor-fold desc="To get recommendation detail by uniqueId">
    public ArrayList<HashMap<String, String>> GetRecommendationDetailByUniqueId(String recommendationUniqueId) {
        wordList = new ArrayList<HashMap<String, String>>();

        selectQuery = "SELECT DISTINCT fa.Title,ifnull(fs.SubActivityName,''), rd.ActivityValue, ifnull(uom.ShortName, uoms.ShortName), rd.UniqueId ,ifnull(rd.FileName,''), rd.WeekNo, ifnull(rd.Remarks,'')  FROM RecommendationDetail rd LEFT OUTER JOIN FarmSubActivity fs ON rd.FarmSubActivityId = fs.FarmSubActivityId LEFT OUTER JOIN UOM uoms ON fs.UOMId = uoms.UOMId, FarmActivity fa LEFT OUTER JOIN UOM uom ON  fa.UOMId = uom.UOMId WHERE rd.FarmActivityId = fa.FarmActivityId AND rd.RecommendationUniqueId = '" + recommendationUniqueId + "' ORDER BY CAST(rd.WeekNo AS NUMERIC) COLLATE NOCASE ASC, fa.Title COLLATE NOCASE ASC, fs.SubActivityName COLLATE NOCASE ASC";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("ActivityName", cursor.getString(0));
            map.put("SubActivityName", cursor.getString(1));
            map.put("Quantity", cursor.getString(2));
            map.put("UOM", cursor.getString(3));
            map.put("UniqueId", cursor.getString(4));
            map.put("FileName", cursor.getString(5));
            map.put("Week", cursor.getString(6));
            map.put("Remark", cursor.getString(7));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if same additional activity is already added added">
    public Boolean isRecommendedAlreadyAdded(String recommendationUniqueId, String weekNo, String activityId, String subActivityId) {
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM RecommendationDetail WHERE RecommendationUniqueId = '" + recommendationUniqueId + "' AND WeekNo = '" + weekNo + "' AND FarmActivityId = '" + activityId + "' AND FarmSubActivityId ='" + subActivityId + "' AND IsTemp ='1'  ";
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get details of Recommendation for Display">
    public List<VisitNurseryData> GetRecommendationByUniqueId(String uniqueId) {
        List<VisitNurseryData> labels = new ArrayList<VisitNurseryData>();
        selectQuery = "SELECT DISTINCT A, B, C, D, E, F FROM (SELECT DISTINCT re.Id, re.UniqueId AS A, fa.Salutation||' '||fa.FirstName||(CASE WHEN fa.MiddleName='' THEN '' ELSE ' '|| fa.MiddleName END)||' '|| fa.LastName AS B, fa.Mobile AS C, fb.FarmBlockCode AS D, va.Title ||' - '||REPLACE(pl.PlantationDate,'/','-') AS E, re.CreateDate AS F FROM Recommendation re LEFT OUTER JOIN Farmer fa ON re.FarmerUniqueId = fa.FarmerUniqueId LEFT OUTER JOIN FarmBlock fb ON re.FarmBlockNurseryUniqueId= fb.FarmBlockUniqueId LEFT OUTER JOIN FarmerPlantation pl ON re.PlantationId = pl.PlantationUniqueId LEFT OUTER JOIN Variety va ON va.Id = pl.CropVarietyId WHERE FarmBlockNurseryType = 'FarmBlock' AND re.UniqueId = '" + uniqueId + "' UNION ALL SELECT DISTINCT re.Id, re.UniqueId AS A, nu.NurseryType AS B, nu.Title AS C, zn.Title AS D, va.Title ||' - '||REPLACE(pl.PlantationDate,'/','-') AS E, re.CreateDate AS F FROM Recommendation re LEFT OUTER JOIN Nursery nu ON re.FarmBlockNurseryUniqueId = nu.Id LEFT OUTER JOIN NurseryZone zn ON re.ZoneId = zn.Id LEFT OUTER JOIN NurseryPlantation pl ON re.PlantationId = pl.PlantationUniqueId LEFT OUTER JOIN Variety va ON va.Id = pl.CropVarietyId WHERE FarmBlockNurseryType = 'Nursery' AND re.UniqueId = '" + uniqueId + "') a ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new VisitNurseryData(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Recommendation For Sync">
    public ArrayList<HashMap<String, String>> GetRecommendationSync() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT FarmBlockNurseryType, FarmBlockNurseryUniqueId, ZoneId, PlantationId, CreateDate, Latitude, Longitude, Accuracy, CreateBy, UniqueId FROM Recommendation WHERE IsSyncData IS NULL AND IsTemp = '0'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("FarmBlockNurseryType", cursor.getString(0));
            map.put("FarmBlockNurseryId", cursor.getString(1));
            map.put("ZoneId", cursor.getString(2));
            map.put("PlantationId", cursor.getString(3));
            map.put("Date", cursor.getString(4));
            map.put("Latitude", cursor.getString(5));
            map.put("Longitude", cursor.getString(6));
            map.put("Accuracy", cursor.getString(7));
            map.put("UserId", cursor.getString(8));
            map.put("UniqueId", cursor.getString(9));
            list.add(map);
        }
        cursor.close();
        return list;
    }
    //</editor-fold>

    //<editor-fold desc="To get all Recommendation For Sync">
    public ArrayList<HashMap<String, String>> GetRecommendationDetailSync() {
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT RecommendationUniqueId, UniqueId, FarmActivityId, FarmSubActivityId, UomId, WeekNo, IFNULL(Remarks,''), ActivityValue, IFNULL(FileName,'') FROM RecommendationDetail WHERE IsSyncData IS NULL AND IsTemp = '0'";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("RecommendationUniqueId", cursor.getString(0));
            map.put("UniqueId", cursor.getString(1));
            map.put("FarmActivityId", cursor.getString(2));
            map.put("FarmSubActivityId", cursor.getString(3));
            map.put("UomId", cursor.getString(4));
            map.put("WeekNo", cursor.getString(5));
            map.put("Remarks", cursor.getString(6));
            map.put("ActivityValue", cursor.getString(7));
            map.put("FileName", cursor.getString(8));
            list.add(map);
        }
        cursor.close();
        return list;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get quantity, remarks by weekno, activity , sub activity and Plantation unique id">
    public String GetQtyRemarksByActSubActId(String weekNo, String plantationUniqueId, String plantationDate, String activityId, String subactivityId) {
        String quantity = "";
        String remarks = "";

        selectQuery = "SELECT Quantity, Remarks FROM PlannedActivity WHERE WeekNo = '" + weekNo + "' AND ActivityId = '" + activityId + "' AND SubActivityId = '" + subactivityId + "' AND PlantationUniqueId = '" + plantationUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                quantity = cursor.getString(0);
                remarks = cursor.getString(1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return quantity + "~" + remarks;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Pending Job Card List for Display">
    public List<PendingJobCardData> getPendingJobCardList() {
        List<PendingJobCardData> labels = new ArrayList<PendingJobCardData>();
        selectQuery = "SELECT DISTINCT fb.FarmBlockCode, jcp.UniqueId, ifnull(fr.FarmerCode,'') AS FarmerCode, fr.Salutation||' '||fr.FirstName||(CASE WHEN fr.MiddleName = '' THEN '' ELSE ' '|| fr.MiddleName END)||' '|| fr.LastName AS FarmerName , fr.FatherSalutation||' '||fr.FatherFirstName||(CASE WHEN fr.FatherMiddleName='' THEN '' ELSE ' '|| fr.FatherMiddleName END)||' '|| fr.FatherLastName AS FatherName, fr.Mobile,(CASE WHEN ad.AddressType ='District Based' THEN ifnull(vi.VillageName,'')||' , '||ifnull(bl.BlockName,'') ELSE ifnull(ci.CityName,'') ||' , '|| ifnull(di.DistrictName,'') END) AS Address, (var.Title||' - '|| pl.PlantationDate) AS Plantation, jcp.PlantationUniqueId FROM Farmer fr, Address ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId, JobCardPending jcp, FarmBlock fb, FarmerPlantation pl, Crop cr, Variety var WHERE fr.FarmerUniqueId = ad.FarmerUniqueId AND fr.FarmerCode IS NOT NULL AND jcp.UniqueId = fb.FarmBlockUniqueId AND jcp.FarmBlockNurseryType = 'FarmBlock' AND fb.FarmerId = fr.FarmerUniqueId AND jcp.PlantationUniqueId = pl.PlantationUniqueId AND pl.CropId = cr.Id AND pl.CropVarietyId = var.Id  ORDER BY fr.FirstName COLLATE NOCASE ASC";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new PendingJobCardData(cursor.getString(0), cursor.getString(1), cursor.getString
                    (2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString
                    (6), cursor.getString(7), cursor.getString(8)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if Job Card Already is already confirmed">
    public Boolean isJobCardAlreayConfirmed(String uniqueId, String plUniqueId) {
        Boolean dataExists = false;
        selectQuery = "SELECT Id FROM JobCardPending WHERE IsSync = 0 AND UniqueId = '" + uniqueId + "' AND PlantationUniqueId = '" + plUniqueId + "'";
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="To get Pending Job Card  By Plantation Unique Id">
    public List<JobCardConfirmationData> getConfirmedJobCard(String plantationUniqueId) {
        List<JobCardConfirmationData> labels = new ArrayList<JobCardConfirmationData>();
        selectQuery = "SELECT jcp.JobCardDetailId, jcp.VisitDate, (CASE WHEN fa.IsSubActivityAllowed = 1 THEN (fa.Title || ' - '  || fsa.SubActivityName) ELSE fa.Title END) AS Activity, jcp.FarmBlockNurseryType, jcp.FarmBlockNurseryId, jcp.ActivityValue, jcp.ConfirmValue, (CASE WHEN jcp.ActivityType = 'P' THEN 'Planned' WHEN jcp.ActivityType = 'A' THEN 'Unplanned' ELSE 'Recommended' END) AS ActivityType, jcp.PlannedValue, (CASE WHEN fa.IsSubActivityAllowed = 1 THEN um1.ShortName ELSE um.ShortName END) AS UOM, jcp.Remarks FROM JobCardPending jcp LEFT OUTER JOIN  FarmActivity fa  ON  jcp.FarmActivityId = fa.FarmActivityId LEFT OUTER JOIN FarmSubActivity fsa ON jcp.FarmSubActivityId = fsa.FarmSubActivityId LEFT OUTER JOIN UOM um ON fa.UomId = um.UomId LEFT OUTER JOIN UOM um1 ON fsa.UomId = um1.UomId WHERE jcp.IsSync = 0 AND jcp.PlantationUniqueId = '" + plantationUniqueId + "' ORDER BY jcp.VisitDate, ActivityType COLLATE NOCASE ASC,  (CASE WHEN fa.IsSubActivityAllowed = 1 THEN (fa.Title || ' - '  || fsa.SubActivityName) ELSE fa.Title END) COLLATE NOCASE ASC";

        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new JobCardConfirmationData(cursor.getString(0), cursor.getString(1), cursor
                    .getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get list of Pending Job Card List for Display">
    public List<PendingNurseryJobCardData> getPendingNurseryJobCardList() {
        List<PendingNurseryJobCardData> labels = new ArrayList<PendingNurseryJobCardData>();
        selectQuery = "SELECT DISTINCT nur.NurseryType, nur.Title AS NurseryName, jcp.UniqueId, nz.Title As NurseryZone, nur.Salutation||' '||nur.FirstName||(CASE WHEN nur.MiddleName = '' THEN '' ELSE ' '|| nur.MiddleName END)||' '|| nur.LastName AS ContactPerson , nur.Mobile, (CASE WHEN ad.AddressType ='District Based' THEN ifnull(vi.VillageName,'')||' , '||ifnull(bl.BlockName,'') ELSE ifnull(ci.CityName,'') ||' , '|| ifnull(di.DistrictName,'') END) AS Address, (var.Title||' - '|| pl.PlantationDate) AS Plantation, jcp.PlantationUniqueId FROM Nursery nur, AddressData ad LEFT OUTER JOIN District di ON ad.DistrictId = di.DistrictId LEFT OUTER JOIN City ci ON ad.CityId = ci.CityId LEFT OUTER JOIN Block bl ON ad.BlockId = bl.BlockId LEFT OUTER JOIN  Village vi ON ad.VillageId = vi.VillageId, JobCardPending jcp, NurseryZone nz, NurseryPlantation pl, Crop cr, Variety var WHERE nur.UniqueId = ad.UniqueId AND jcp.UniqueId = nur.UniqueId AND jcp.FarmBlockNurseryType = 'Nursery' AND jcp.PlantationUniqueId = pl.PlantationUniqueId AND pl.CropId = cr.Id AND pl.CropVarietyId = var.Id AND nz.Id = jcp.ZoneId  ORDER BY nur.NurseryType, nur.Title, nz.Title  COLLATE NOCASE ASC";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new PendingNurseryJobCardData(cursor.getString(0), cursor.getString(1), cursor.getString
                    (2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString
                    (6), cursor.getString(7), cursor.getString(8)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="To get Pending Job Card  By Plantation Unique Id">
    public List<JobCardConfirmationData> getPendingJobCard(String plantationUniqueId) {
        List<JobCardConfirmationData> labels = new ArrayList<JobCardConfirmationData>();
        selectQuery = "SELECT jcp.JobCardDetailId, jcp.VisitDate, (CASE WHEN fa.IsSubActivityAllowed = 1 THEN (fa.Title || ' - '  || fsa.SubActivityName) ELSE fa.Title END) AS Activity, jcp.FarmBlockNurseryType, jcp.FarmBlockNurseryId, jcp.ActivityValue, 0, (CASE WHEN jcp.ActivityType = 'P' THEN 'Planned' WHEN jcp.ActivityType = 'A' THEN 'Unplanned' ELSE 'Recommended' END) AS ActivityType, jcp.PlannedValue, (CASE WHEN fa.IsSubActivityAllowed = 1 THEN um1.ShortName ELSE um.ShortName END) AS UOM, jcp.Remarks FROM JobCardPending jcp LEFT OUTER JOIN  FarmActivity fa  ON  jcp.FarmActivityId = fa.FarmActivityId LEFT OUTER JOIN FarmSubActivity fsa ON jcp.FarmSubActivityId = fsa.FarmSubActivityId LEFT OUTER JOIN UOM um ON fa.UomId = um.UomId LEFT OUTER JOIN UOM um1 ON fsa.UomId = um1.UomId WHERE jcp.PlantationUniqueId = '" + plantationUniqueId + "' ORDER BY jcp.VisitDate, ActivityType COLLATE NOCASE ASC,  (CASE WHEN fa.IsSubActivityAllowed = 1 THEN (fa.Title || ' - '  || fsa.SubActivityName) ELSE fa.Title END) COLLATE NOCASE ASC";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            labels.add(new JobCardConfirmationData(cursor.getString(0), cursor.getString(1), cursor
                    .getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10)));
        }
        cursor.close();
        return labels;
    }
    //</editor-fold>

    //<editor-fold desc="To get all confirmed job card For Sync">
    public ArrayList<HashMap<String, String>> getConfimedPendingJobCardForSync() {
        ArrayList<HashMap<String, String>> wordList = new ArrayList<HashMap<String, String>>();
        selectQuery = "SELECT JobCardId, FarmBlockNurseryType, UniqueId, PlantationUniqueId, JobCardDetailId, ConfirmValue, CreateDate FROM JobCardPending WHERE IsSync = 0";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            map = new HashMap<String, String>();
            map.put("JobCardId", cursor.getString(0));
            map.put("FarmBlockNurseryType", cursor.getString(1));
            map.put("UniqueId", cursor.getString(2));
            map.put("PlantationUniqueId", cursor.getString(3));
            map.put("JobCardDetailId", cursor.getString(4));
            map.put("ConfirmValue", cursor.getString(5));
            map.put("CreateDate", cursor.getString(6));
            wordList.add(map);
        }
        cursor.close();
        return wordList;
    }
    //</editor-fold>

    //<editor-fold desc="Code to check if farmer Edit Is Allowed">
    public Boolean isFarmerEditable(String farmerUniqueId) {
        Boolean dataExists = false;
        selectQuery = "SELECT DISTINCT f.FarmerUniqueId FROM Farmer f, FarmerOperatingBlocks ob, UserRoles ur WHERE f.FarmerUniqueId = ob.FarmerUniqueId AND ob.BlockId = ur.BlockId AND ur.RoleName ='Service Provider' AND f.FarmerUniqueId = '" + farmerUniqueId + "' ";
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            dataExists = true;
        }
        cursor.close();
        return dataExists;
    }
    //</editor-fold>

    //<editor-fold desc="Code to get area by farm block unique id">
    public String GetAreaByFarmBlockUniqueId(String farmBlockUniqueId) {
        String area = "";
        selectQuery = "SELECT Acerage FROM FarmBlock WHERE FarmBlockUniqueId = '" + farmBlockUniqueId + "' ";

        cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                area = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return area;
    }
    //</editor-fold>


    //<editor-fold desc="Code to get farmer details For Farmer Login">
    public ArrayList<String> getFarmerDetailsForFarmerLogin() {
        ArrayList<String> farmerdetails = new ArrayList<String>();
        String newFileName = "";
        selectQuery = "SELECT DISTINCT fr.FarmerUniqueId,fr.Salutation||' '||fr.FirstName||(CASE WHEN fr.MiddleName='' THEN '' ELSE ' '|| fr.MiddleName END)||' '|| fr.LastName AS FarmerName,fr.Mobile  FROM Farmer fr  ";
        cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            farmerdetails.add(cursor.getString(0));
            farmerdetails.add(cursor.getString(1));
            farmerdetails.add(cursor.getString(2));
        }
        cursor.close();

        return farmerdetails;
    }
    //</editor-fold>
//--------------------------------------------End of Select Queries---------------------------------------------//
}
