USE [wsp]
GO

/****** Object:  StoredProcedure [dbo].[AddItem]    Script Date: 2021/4/6 16:22:05 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



ALTER PROCEDURE [dbo].[AddItem]
	@item_name nvarchar(20)
,	@item_date date
,	@categ nvarchar(20)
,	@descr nvarchar(50)
,	@value1 int
,	@value2 decimal(12,4)
,	@more nvarchar(50)
,	@item_id int OUTPUT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	DECLARE @dttm datetime = sysdatetime();

	select @item_id = max(item_id)+1 from MiscItems;

	INSERT INTO [dbo].[MiscItems]
           ([item_id]
           ,[item_name]
           ,[item_date]
           ,[categ]
           ,[descr]
           ,[valueN1]
           ,[valueD1]
           ,[dttm]
           ,[more])
    VALUES
           (@item_id
           ,@item_name
		   ,@item_date
           ,@categ
           ,@descr
           ,@value1
           ,@value2
           ,@dttm
           ,@more)

	RETURN @item_id
END



GO

