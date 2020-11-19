USE [wsp]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[AllChargingPoints] 
AS
BEGIN
	SET NOCOUNT OFF;

	select
		item_name deviceId
	,	descr name
	,	cast(valueD1/100 as decimal(8,6)) latitude
	,	cast(valueD2/100 as decimal(8,6)) longitude 
	from MiscItems where categ = 'CHARGINGPOINT'
	
	return @@ROWCOUNT

END

GO
