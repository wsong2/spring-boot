USE [wsp]
GO

/****** Object:  StoredProcedure [dbo].[AddChargingPoint]    Script Date: 2019/3/18 14:22:31 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


ALTER PROCEDURE [dbo].[AddChargingPoint]
	@chargeDeviceID nvarchar(20)
,	@reference nvarchar(20)
,	@longitude decimal(12,4)
,	@latitude decimal(16,6)
,	@indicator int OUTPUT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	Declare @categ nvarchar(20) = 'CHARGINGPOINT';
	Declare @LongitudeValue	decimal(12,4) = @longitude * 100.0;
	Declare @LatitudeValue	decimal(16,6) = @latitude * 100.0;

	Declare @ItemId	int;

	select @ItemId = item_id
	from MiscItems 
	where [categ] = @categ and [item_name] = @chargeDeviceID;

	IF @@ROWCOUNT = 0
	begin	
		select @ItemId = isnull(max(item_id),0)+1 from MiscItems;

		INSERT INTO [dbo].[MiscItems]
			   ([item_id]
			   ,[item_name]
			   ,[categ]
			   ,[descr]
			   ,[valueD1]
			   ,[valueD2])
		VALUES (@ItemId
			   ,@chargeDeviceID
			   ,@categ
			   ,@reference
			   ,@LongitudeValue
			   ,@LatitudeValue)
		
		SELECT @indicator = 1
		RETURN @ItemId
	end

	SELECT @indicator = 0
	RETURN 0
END

GO

