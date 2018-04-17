res1 = csvread('res.csv');
res2 = csvread('resvfs.csv');
ds_label = {'Dataset 1', 'Dataset 2'};
hand_label = {'Left Hand', 'Right Hand'};
close all;

summary = [];

for pose = 0:1 
    sds = [];
    for ds =1:2
        s = [];
        for hand = 1:2        
            r = res1;
            r = r(r(:, 1)==ds & r(:, 2)==hand & r(:, 3)==pose, end-2:end);
            s1 = mean(r);
            %summary = [summary; ds, hand, pose, 1, r];
                       
            r = res2;
            r = r(r(:, 1)==ds & r(:, 2)==hand & r(:, 3)==pose, end-2:end);
            s2 = mean(r)
            %summary = [summary; ds, hand, pose, 2, r];
            
            if~isempty(s)
                s = (s+[s1', s2'])/2;            
            else
                s = [s1', s2'];
            end
        end
        sds = [sds;s];
    end
    
    figure;
    bar(sds)
    ylim([0.4, 1]);
    legend('Harmony','HarmonySTS');       
    %set(gca,'XTickLabel',{'Precision(DS1)', 'Recall(DS1)', 'F1-Score(DS1)', 'Precision(DS2)', 'Recall(DS2)', 'F1-Score(DS2)'})
    set(gca,'XTickLabel',{'Precision', 'Recall', 'F1-Score', 'Precision', 'Recall', 'F1-Score'})
    
    
    
    
end



