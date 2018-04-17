function readAndSaveHWData()
if exist('hwData.mat','file') == 2    
    return
end
display('Reading data ...');
gestures = {'1', '2_1', '2_2', '3', '4', '5_1', '5_2', '6_1', '6_2', '7', 'mix_hw', 'not_hw' };
hands = {'left', 'right'};
hwDataLeft = readData(1:7, 1:5, gestures(1:12), hands(1));
hwDataRight = readData(1:7, 1:5, gestures(1:12), hands(2));
save('hwData', 'hwDataLeft','hwDataRight');
display('Reading data done.');
end

function data = readData(subjects, times, gestures, hand)
slen = length(subjects);
tlen = length(times);
glen = length(gestures);
data = [];
for i=1:slen  % subjects
    for j=1:tlen % times
        for k = 1:glen %length(gestures)-1            
            fname = strcat('data/handwash_text_processed/sub_', num2str(i),'_', hand ,'_',num2str(j),'_pose_',gestures(k));
            fname = char(fname);
            temp = csvread(fname);
            len = size(temp, 1);
            s = zeros(len, 1) + i;
            t = zeros(len,1) + j;
            g = zeros(len,1) + k;            
            data = [data ; temp s t g];            
           
        end
    end    
end

end