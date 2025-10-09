-- Script completo de NovaERP para SQL Server.
-- Ejecute en SSMS. Ajuste los nombres y las longitudes segun sus necesidades.
-- Creado por: Noah Lecegui.
-- Fecha 09/10/2025.

---------------------------------------------------------
-- 1) Crear base de datos (opcional) y usarla
---------------------------------------------------------
IF DB_ID(N'NovaERP') IS NULL
BEGIN
	CREATE DATABASE NovaERP
	COLLATE Latin1_General_CI_AS;
END
GO

USE NovaERP;
GO

---------------------------------------------------------
-- 2) Crear esquemas
---------------------------------------------------------
CREATE SCHEMA security;
CREATE SCHEMA hr;
CREATE SCHEMA inventory;
CREATE SCHEMA sales;
CREATE SCHEMA purchases;
CREATE SCHEMA finance;
CREATE SCHEMA system;
GO

---------------------------------------------------------
-- 3) Tipo comunes y tablas de catálogo
---------------------------------------------------------
CREATE TABLE system.Settings (
	SettingKey NVARCHAR(100) PRIMARY KEY,
	SettingValue NVARCHAR(MAX),
	UpdatedAt DATETIME2 DEFAULT SYSUTCDATETIME()
);

CREATE TABLE system.Currency (
	CurrencyCode CHAR(3) PRIMARY KEY,
	Name NVARCHAR(MAX),
	Symbol NVARCHAR(10)
);

CREATE TABLE system.Tax (
	TaxId INT IDENTITY(1,1) PRIMARY KEY,
	Name NVARCHAR(100) NOT NULL,
	Rate DECIMAL(10,4) NOT NULL,
	IsDefault BIT DEFAULT 0
);
GO