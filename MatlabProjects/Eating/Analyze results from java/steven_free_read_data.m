srcFolder = 'C:\ASM\DevData\eating\java\text_data_for_matlab\';
destFile = 'C:\ASM\DevData\eating\matlab\steven_data_results';

data = [];
for subject = 0:10
    for sess = 0:4
        if subject~=9 && sess>=2
            continue
        end
        fprintf('Reading data: %d, %d\n', subject, sess);
        data(subject+1).session(sess+1).accel = csvread(strcat(srcFolder, 'steven_free_accel_', num2str(subject), '_', num2str(sess),'.csv'));
        data(subject+1).session(sess+1).meals = csvread(strcat(srcFolder, 'steven_free_meals_', num2str(subject), '_', num2str(sess),'.csv'));
        data(subject+1).session(sess+1).retrain(1).res = csvread(strcat(srcFolder, 'steven_res0_meals_', num2str(subject), '_', num2str(sess),'.csv'));
        data(subject+1).session(sess+1).retrain(2).res = csvread(strcat(srcFolder, 'steven_res1_meals_', num2str(subject), '_', num2str(sess),'.csv'));
        %data(subject+1).session(sess+1).retrain(3).res = csvread(strcat(srcFolder, 'steven_res2_meals_', num2str(subject), '_', num2str(sess),'.csv'));
        %data(subject+1).session(sess+1).retrain(4).res = csvread(strcat(srcFolder, 'steven_res3_meals_', num2str(subject), '_', num2str(sess),'.csv'));        
    end
end

save('C:\ASM\DevData\eating\matlab\steven_data_results', 'data');