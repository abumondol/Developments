load('allres');
alpha_vals = 1:1:4;

db = allres{1, 1}.ward_res_base;
for k=1:5    
    d1 = [];
    d2 = [];
    d = db;
    for aix = 1:length(alpha_vals)        
        d = [d; allres{k, aix}.ward_res_ours1];
        d = [d; allres{k, aix}.ward_res_ours2];
    end
    
    figure    
    bar(d, 'stacked');
    %xlabel('\alpha', 'FontSize', 20);
    ylim([0, 120]);
    xticklabels({'B', 'A1_1', 'A2_1', 'A1_2', 'A2_2', 'A1_3', 'A2_3', 'A1_4', 'A2_4'});
    title(strcat('k=',num2str(k),''), 'FontSize', 12);
    lgd = legend('Subs', 'Del', 'Fra', 'Ins', 'Mer', 'Und', 'Ove','TP');
    lgd.FontSize = 12;
    lgd.Orientation = 'horizontal';
    
    
end

