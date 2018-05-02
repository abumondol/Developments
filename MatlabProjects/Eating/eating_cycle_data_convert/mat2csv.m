path = 'C:\\Users\\mm5gg\\Downloads\\Food_Intake_Cycle_Dataset\\Food_Intake_Cycle_Dataset\\'

for i=1:9
    display(i)
    load(strcat(path,'Data\\S0', num2str(i), '.mat'))
    load(strcat(path,'GT\\GT_S0', num2str(i), '.mat'))
    dlmwrite(strcat(path,'Data\\accel_S0', num2str(i), '.csv'), acceleration, 'delimiter',',','precision',15)
    dlmwrite(strcat(path,'Data\\gyro_S0', num2str(i), '.csv'), gyroscope, 'delimiter',',','precision',15)
    dlmwrite(strcat(path,'GT\\GT_S0', num2str(i), '.csv'), GT, 'delimiter',',','precision',15)    
end

i = 10
load(strcat(path,'Data\\S', num2str(i), '.mat'))
load(strcat(path,'GT\\GT_S', num2str(i), '.mat'))
dlmwrite(strcat(path,'Data\\accel_S', num2str(i), '.csv'), acceleration, 'delimiter',',','precision',15)
dlmwrite(strcat(path,'Data\\gyro_S', num2str(i), '.csv'), gyroscope, 'delimiter',',','precision',15)
dlmwrite(strcat(path,'GT\\GT_S', num2str(i), '.csv'), GT, 'delimiter',',','precision',15)    