-- Program realizat dupa modelul de la adresa: 
-- https://springrts.com/wiki/Lua:Tutorial_GettingStarted


function widget:GetInfo()
    return {
        name = "Game_Results_Licenta",
        desc = "Returning the game results",
        author = "CAD",
        date = "now",
        license = "None",
        layer = 0,
        enabled = true
    }
end


local startMsg = "[game_results_licenta.lua] Game_Results_Licenta STARTED!"
local teams = {}
local Echo = Spring.Echo


function widget:Initialize()
	Echo(startMsg)
    local getTeams = Spring.GetTeamList()

	for _,teamID in pairs(getTeams) do
        local team = {}
        
		team.teamId,team.leader,team.isDead,team.isAiTeam,team.side,team.allyTeam,team.customTeamKeys = Spring.GetTeamInfo(teamID)
    
        if team.isAiTeam then
			local shortName
			local version
            
            _,_,_,shortName,_,_ = Spring.GetAIInfo(teamID)
            team.name = shortName
            
			teams[teamID] = team
        end
    end
    
end


function GetCurrentTeamInfo(currentTeam)
    currentTeam.currentLevelMetal, currentTeam.storageMetal, currentTeam.pullMetal, currentTeam.incomeMetal, currentTeam.expenseMetal, currentTeam.shareMetal, currentTeam.sentMetal, currentTeam.receivedMetal = Spring.GetTeamResources(currentTeam.teamId, "metal")
    currentTeam.currentLevelEnergy, currentTeam.storageEnergy, currentTeam.pullEnergy, currentTeam.incomeEnergy, currentTeam.expenseEnergy, currentTeam.shareEnergy, currentTeam.sentEnergy, currentTeam.receivedEnergy = Spring.GetTeamResources(currentTeam.teamId, "energy")
       
    currentTeam.metalUsed, currentTeam.metalProduced, currentTeam.metalExcessed, currentTeam.metalReceived, currentTeam.metalSent = Spring.GetTeamResourceStats(currentTeam.teamId, "metal")
    currentTeam.energyUsed, currentTeam.energyProduced, currentTeam.energyExcessed, currentTeam.energyReceived, currentTeam.energySent = Spring.GetTeamResourceStats(currentTeam.teamId, "energy")
        
    currentTeam.unitsKilled, currentTeam.unitsDied, currentTeam.unitsCapturedBy, currentTeam.unitsCapturedFrom, currentTeam.unitsReceived, currentTeam.unitsSent = Spring.GetTeamUnitStats(currentTeam.teamId)
end


function widget:TeamDied(teamID)
	local currentTeam = teams[teamID]
	if currentTeam then
        currentTeam.timeOfDeath = Spring.GetGameSeconds()
        GetCurrentTeamInfo(currentTeam)
	end
end


function widget:Shutdown()
	if # teams > 0 then
		local file = io.open ("./game_results/game_results_licenta.csv", "w")
        if  file ~= nil then
            file:write("teamId,name,gameStatus,timeOfDeath,")
            file:write("currentLevelMetal,storageMetal,pullMetal,incomeMetal,expenseMetal,")
            file:write("currentLevelEnergy,storageEnergy,pullEnergy,incomeEnergy,expenseEnergy,")
            file:write("metalUsed,metalProduced,")
            file:write("energyUsed,energyProduced,")
            file:write("unitsKilled,unitsDied")
            file:write("\n")


            for i,team in pairs(teams) do
				if team.timeOfDeath then
                    team.gameStatus = "lost"
                else
                    team.gameStatus = "won"
                    GetCurrentTeamInfo(team)
                    team.timeOfDeath = Spring.GetGameSeconds()
                end

                file:write(team.teamId .. "," .. team.name .. "," .. team.gameStatus .. "," .. team.timeOfDeath .. ",")
                file:write(team.currentLevelMetal .. "," .. team.storageMetal .. "," .. team.pullMetal .. "," .. team.incomeMetal .. "," .. team.expenseMetal .. ",")
                file:write(team.currentLevelEnergy .. "," .. team.storageEnergy .. "," .. team.pullEnergy .. "," .. team.incomeEnergy .. "," .. team.expenseEnergy .. ",")
                file:write(team.metalUsed .. "," .. team.metalProduced .. ",")
                file:write(team.energyUsed .. "," .. team.energyProduced .. ",")
                file:write(team.unitsKilled .. "," .. team.unitsDied)
                file:write("\n")

			end
			file:close()
		else
			Echo("[game_results_licenta.lua] Failed to open status file !")
		end
	end
end


