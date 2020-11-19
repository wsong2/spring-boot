USE [wsp]
GO

/****** Object:  StoredProcedure [dbo].[DeleteChargingPoint]    Script Date: 2019/3/23 8:55:55 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
ALTER PROCEDURE [dbo].[DeleteChargingPoint] 
	@chargeDeviceID nvarchar(20)
,	@indicator int OUTPUT
AS
BEGIN
	SET NOCOUNT OFF;

	Declare @categ nvarchar(20) = 'CHARGINGPOINT';

    DELETE FROM [dbo].[MiscItems] WHERE [categ] = @categ and [item_name] = @chargeDeviceID;

	SELECT @indicator = @@ROWCOUNT

	RETURN @indicator
END

GO

