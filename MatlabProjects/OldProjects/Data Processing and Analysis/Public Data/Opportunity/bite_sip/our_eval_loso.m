res = loso_gxmin;

% %%%%%%%%theta mds
% for a=1:6
%     d = [];
%     for b=1:9
%         d = [d;res{a,b}];        
%     end
%     figure
%     theta = 10:10;90;    
%     plot( d(:,1:3));
%     xticklabels({'10','20','30','40','50','60','70','80','90'});
%     
%     xlabel('\theta (degree)', 'FontSize', 14);
%     %ylabel('%', 'FontSize', 14);
%     ylim([0,1]);
%     lgd = legend('Precision', 'Recall', 'F-score');
%     legend('Location','southeast')
%     lgd.FontSize = 14;    
%     title(strcat('MFD = ',num2str(a+4), ' second'));
% end
% 
% %%%%%%%%%%%%%%%%%%%%theta mds
% d = [];
% for a=1:6
%     
%     d = [d;res{a,7}];
% end
% figure    
% plot( [5:10]', d(:,1:3));
% 
% xlabel('MFD (seconds)', 'FontSize', 14);
% ylim([0,1]);
% lgd = legend('Precision', 'Recall', 'F-score');
% legend('Location','southeast')
% lgd.FontSize = 14;    
% title(strcat('\theta = ',num2str(70), ' degree'));

%%%%%%%%%%%%%%%%%%%%gxmin
d = [];
for i=1:8    
    d = [d;res{i,:}];
end
figure
fnr = d(:,7)./(d(:,7)+d(:,6)+d(:,7));
t = 0:0.1:0.7;
plot( t, d(:,[1:3]));
hold on
plot( t, fnr);

xlabel('g_{xmin}', 'FontSize', 14);
ylim([0,1]);
lgd = legend('Precision', 'Recall', 'F-score', 'FNR_1');
legend('Location','southeast')
lgd.FontSize = 14;    
%title(strcat('\theta = ',num2str(70), ' degree'));

%%%%%%%%%%%%%%%%%%%
res = res2;
d = [];
for i=1:10    
    d = [d;res{1,i}];
end
ores_loso = d;
save('ores_loso','ores_loso');

%%%%%%%%%%%%
d = ores_loso;
our = d(:,1:3);
base = d(:, 4:6);

figure
bar([base(:,1), our(:,1)])
ylim([0.7,1]);
xticklabels({'10','20','30','40','50','60','70','80','90', '100'});
legend('Baseline','Our solution');
title('Precision');
xlabel('Number of tree');

figure
bar([base(:,2), our(:,2)])
ylim([0.5,1]);
xticklabels({'10','20','30','40','50','60','70','80','90', '100'});
legend('Baseline','Our solution');
title('Recall');
xlabel('Number of tree');

figure
bar([base(:,3), our(:,3)])
ylim([0.7,1]);
xticklabels({'10','20','30','40','50','60','70','80','90', '100'});
legend('Baseline','Our solution');
title('F-score');
xlabel('Number of tree');




figure
fnr = d(:,7)./(d(:,7)+d(:,6)+d(:,7));
t = 0:0.1:0.7;
plot( t, d(:,[1:3]));
hold on
plot( t, fnr);

xlabel('g_{xmin}', 'FontSize', 14);
ylim([0,1]);
lgd = legend('Precision', 'Recall', 'F-score', 'FNR_1');
legend('Location','southeast')
lgd.FontSize = 14;    
%title(strcat('\theta = ',num2str(70), ' degree'));


